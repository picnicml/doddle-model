package io.picnicml.doddlemodel.data

import java.io.File

import breeze.linalg.DenseMatrix
import com.github.tototoshi.csv.CSVReader

import scala.collection.mutable
import scala.util.control.Exception.nonFatalCatch

object CsvLoader {

  private sealed trait FeatureType extends Product with Serializable {
    val headerLineString: String
  }

  private final case object NumericalFeature extends FeatureType {
    override val headerLineString = "n"
  }

  private final case object CategoricalFeature extends FeatureType {
    override val headerLineString = "c"
  }

  /** Loads a csv dataset with 2 header lines (feature names and types). */
  def loadCsvDataset(datasetFile: File, naString: String = "NA"): DenseMatrix[Double] = {
    val reader = CSVReader.open(datasetFile)

    val headerLine = inferHeaderLine(reader)
    val labelEncoder = inferLabelEncoder(datasetFile, naString, headerLine)

    val data = reader.toStream.map { rowValues =>
      rowValues.zipWithIndex.map { case (featureValue, featureIndex) =>
        headerLine.featureTypes(featureIndex) match {
          case _ if featureValue == naString => Double.NaN
          case NumericalFeature => parseDouble(featureValue, headerLine, featureIndex)
          case CategoricalFeature => labelEncoder.encode(headerLine.featureNames(featureIndex), featureValue)
        }
      }.toArray
    }

    val dataset = DenseMatrix(data:_*)
    reader.close()
    dataset
  }

  private def inferHeaderLine(reader: CSVReader): HeaderLine = {
    val featureNames = reader.readNext
      .fold(throw new IllegalArgumentException("File has a missing header line: feature names"))(_.toArray)

    val featureTypes = reader.readNext.fold {
      throw new IllegalArgumentException("File has a missing header line: feature types")
    } {
      typeStrings => typeStrings.map { typeString =>
        if (typeString == NumericalFeature.headerLineString)
          NumericalFeature
        else if (typeString == CategoricalFeature.headerLineString)
          CategoricalFeature
        else
          throw new IllegalArgumentException("File contains invalid feature type encoding (second header line)")
      }.toArray
    }

    new HeaderLine(featureNames, featureTypes)
  }

  private class HeaderLine(val featureNames: Array[String], val featureTypes: Array[FeatureType]) {
    def featureIsCategorical(featureIndex: Int): Boolean =
      featureTypes(featureIndex).isInstanceOf[CategoricalFeature.type]
  }

  private def inferLabelEncoder(datasetFile: File, naString: String, headerLine: HeaderLine): LabelEncoder = {
    val reader = CSVReader.open(datasetFile)
    // skip the two header lines
    reader.readNext
    reader.readNext

    val categoricalFeaturesIndices: List[Int] = headerLine.featureNames
      .zipWithIndex
      .filter { case (_, featureIndex) => headerLine.featureIsCategorical(featureIndex) }
      .map { case (_, featureIndex: Int) => featureIndex }
      .toList

    val encoder = mutable.AnyRefMap[String, mutable.AnyRefMap[String, Double]]()
    headerLine.featureNames
      .zipWithIndex
      .filter { case (_, featureIndex) => headerLine.featureIsCategorical(featureIndex) }
      .foreach { case (featureName, _) => encoder(featureName) = mutable.AnyRefMap[String, Double]() }

    reader.toStream.foreach { rowValues =>
      val rowValuesArray = rowValues.toArray
      categoricalFeaturesIndices.foreach { featureIndex =>
        val featureName = headerLine.featureNames(featureIndex)
        val featureValue = rowValuesArray(featureIndex)
        if (featureValue != naString && !encoder(featureName).contains(featureValue))
          encoder(featureName)(featureValue) = encoder(featureName).size
      }
    }

    reader.close()
    new LabelEncoder(encoder)
  }

  private class LabelEncoder(private val encoder:  mutable.AnyRefMap[String, mutable.AnyRefMap[String, Double]]) {
    def encode(featureName: String, featureValue: String): Double = encoder(featureName)(featureValue)
  }

  private def parseDouble(featureValue: String, headerLine: HeaderLine, featureIndex: Int): Double = {
    val parsed = nonFatalCatch opt featureValue.toDouble
    parsed
      .fold(throw new IllegalArgumentException(
        s"Numerical feature ${headerLine.featureNames(featureIndex)} contains non-numerical values"))(identity)
  }
}
