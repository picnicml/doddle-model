package io.picnicml.doddlemodel.data

import breeze.linalg.DenseMatrix
import io.picnicml.doddlemodel.data.Feature.{CategoricalFeature, FeatureIndex, NumericalFeature}

import scala.collection.compat.immutable.ArraySeq
import scala.collection.mutable
import scala.io.{BufferedSource, Source}

object CsvLoader {

  /** Loads a csv dataset with 2 header lines (1st line for feature names and 2nd for types). */
  def loadCsvDataset(datasetFilePath: String, na: String = "NA"): FeaturesWithIndex =
    loadCsvDataset(Source.fromFile(datasetFilePath), na)

  private[doddlemodel] def loadCsvDataset(bufferedSource: BufferedSource, na: String): FeaturesWithIndex = {
    val lines = bufferedSource.getLines()
    val featureIndex = inferFeatureIndex(lines)

    val data = if (featureIndex.types.contains(CategoricalFeature))
      loadWithMixedFeatures(lines.toList, na, featureIndex)
    else
      loadWithNumericalFeatures(lines, na, featureIndex)

    bufferedSource.close()
    (DenseMatrix(ArraySeq.unsafeWrapArray(data):_*), featureIndex)
  }

  private def inferFeatureIndex(lines: Iterator[String]): FeatureIndex = {
    if (!lines.hasNext)
      throw new IllegalArgumentException("File has a missing header line: feature names")
    val featureNames = lines.next().split(",").map(x => removeQuotes(x)).toList

    if (!lines.hasNext)
      throw new IllegalArgumentException("File has a missing header line: feature types")
    val featureTypes = lines.next().split(",").map(x => removeQuotes(x)).map {
      case x if x == NumericalFeature.headerLineString => NumericalFeature
      case x if x == CategoricalFeature.headerLineString => CategoricalFeature
      case _ => throw new IllegalArgumentException("File contains invalid feature type encoding (header line)")
    }.toList

    FeatureIndex(featureNames, featureTypes, featureNames.indices.toList)
  }

  private def loadWithNumericalFeatures(lines: Iterator[String],
                                        na: String,
                                        featureIndex: FeatureIndex): Array[Array[Float]] = {
    lines.map(_.split(",").map { featureValue =>
      val trimmedValue = removeQuotes(featureValue)
      if (trimmedValue == na) Float.NaN else parseFloat(trimmedValue)
    }).toArray
  }

  private def loadWithMixedFeatures(lines: List[String],
                                    na: String,
                                    featureIndex: FeatureIndex): Array[Array[Float]] = {
    val labelEncoder = inferLabelEncoder(lines, na, featureIndex)
    lines.map { rowValues =>
      rowValues.split(",").zipWithIndex.map { case (featureValue, columnIndex) =>
        val trimmedValue = removeQuotes(featureValue)
        featureIndex.types(columnIndex) match {
          case _ if trimmedValue == na => Float.NaN
          case NumericalFeature => parseFloat(trimmedValue)
          case CategoricalFeature => labelEncoder.encode(trimmedValue, featureIndex.names(columnIndex))
        }
      }
    }.toArray
  }

  /** Constructs a label encoder for the given dataset. **/
  private def inferLabelEncoder(lines: List[String], na: String, featureIndex: FeatureIndex): LabelEncoder = {
    val encoder = mutable.AnyRefMap[String, mutable.AnyRefMap[String, Float]]()
    val categoricalFeatures = featureIndex.categorical
    categoricalFeatures.names.foreach { name => encoder(name) = mutable.AnyRefMap[String, Float]() }

    lines.foreach { rowValues =>
      val rowValuesArray = rowValues.split(",").map(x => removeQuotes(x))
      categoricalFeatures.columnIndices.zip(categoricalFeatures.names).foreach { case (columnIndex, name) =>
        val featureValue = rowValuesArray(columnIndex)
        if (featureValue != na && !encoder(name).contains(featureValue))
          encoder(name)(featureValue) = encoder(name).size.toFloat
      }
    }

    new LabelEncoder(encoder)
  }

  /**
    * A mechanism to encode non-numerical feature values (categorical) to numerical values.
    *
    * @param encoder a map containing mapping of categorical values to numerical values for all categorical features
    */
  private class LabelEncoder(private val encoder:  mutable.AnyRefMap[String, mutable.AnyRefMap[String, Float]]) {
    def encode(featureValue: String, featureName: String): Float = encoder(featureName)(featureValue)
  }

  private def removeQuotes(s: String): String =
    s.replaceAll("\"", "").replaceAll("'", "")

  private def parseFloat(featureValue: String): Float = {
    try
      featureValue.toFloat
    catch {
      case _: NumberFormatException =>
        throw new IllegalArgumentException(
          "Numerical feature contains non-numerical values, perhaps type should be 'c'?"
        )
    }
  }
}
