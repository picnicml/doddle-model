package io.picnicml.doddlemodel.data

import java.io.File

import breeze.linalg.DenseMatrix
import com.github.tototoshi.csv.CSVReader
import io.picnicml.doddlemodel.data.Feature.{CategoricalFeature, FeatureIndex, NumericalFeature}

import scala.collection.mutable
import scala.util.control.Exception.nonFatalCatch

object CsvLoader {

  /** Loads a csv dataset with 2 header lines (1st line for feature names and 2nd for types). */
  def loadCsvDataset(datasetFile: File, naString: String = "NA"): FeaturesWithIndex = {
    val reader = CSVReader.open(datasetFile)

    val featureIndex = inferFeatureIndex(reader)
    val labelEncoder = inferLabelEncoder(datasetFile, naString, featureIndex)

    val data = reader.toStream.map { rowValues =>
      rowValues.zipWithIndex.map { case (featureValue, columnIndex) =>
        featureIndex.types(columnIndex) match {
          case _ if featureValue == naString => Double.NaN
          case NumericalFeature => parseDouble(featureValue, featureIndex.names(columnIndex))
          case CategoricalFeature => labelEncoder.encode(featureValue, featureIndex.names(columnIndex))
        }
      }.toArray
    }

    val dataset = DenseMatrix(data:_*)
    reader.close()

    (dataset, featureIndex)
  }

  private def inferFeatureIndex(reader: CSVReader): FeatureIndex = {
    val featureNames = reader.readNext
      .fold(throw new IllegalArgumentException("File has a missing header line: feature names"))(identity)

    val featureTypes = reader.readNext.fold {
      throw new IllegalArgumentException("File has a missing header line: feature types")
    } {
      typeStrings => typeStrings.map {
        case x if x == NumericalFeature.headerLineString => NumericalFeature
        case x if x == CategoricalFeature.headerLineString => CategoricalFeature
        case _ => throw new IllegalArgumentException("File contains invalid feature type encoding (second header line)")
      }
    }

    FeatureIndex(featureNames, featureTypes, featureNames.indices.toList)
  }

  private def inferLabelEncoder(datasetFile: File, naString: String, featureIndex: FeatureIndex): LabelEncoder = {
    val reader = CSVReader.open(datasetFile)
    // skip the two header lines
    reader.readNext
    reader.readNext

    val encoder = mutable.AnyRefMap[String, mutable.AnyRefMap[String, Double]]()
    val categoricalFeatures = featureIndex.categorical
    categoricalFeatures.names.foreach { name => encoder(name) = mutable.AnyRefMap[String, Double]() }

    reader.toStream.foreach { rowValues =>
      val rowValuesArray = rowValues.toArray
      categoricalFeatures.columnIndices.zip(categoricalFeatures.names).foreach { case (columnIndex, name) =>
        val featureValue = rowValuesArray(columnIndex)
        if (featureValue != naString && !encoder(name).contains(featureValue))
          encoder(name)(featureValue) = encoder(name).size
      }
    }

    reader.close()
    new LabelEncoder(encoder)
  }

  private class LabelEncoder(private val encoder:  mutable.AnyRefMap[String, mutable.AnyRefMap[String, Double]]) {
    def encode(featureValue: String, featureName: String): Double = encoder(featureName)(featureValue)
  }

  private def parseDouble(featureValue: String, featureName: String): Double = {
    val parsed = nonFatalCatch opt featureValue.toDouble
    parsed.fold(
      throw new IllegalArgumentException(s"Numerical feature $featureName contains non-numerical values"))(identity)
  }
}
