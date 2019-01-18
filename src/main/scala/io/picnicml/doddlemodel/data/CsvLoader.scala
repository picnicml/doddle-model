package io.picnicml.doddlemodel.data

import java.io.File

import breeze.linalg.DenseMatrix
import com.github.tototoshi.csv.CSVReader

import scala.util.control.Exception.nonFatalCatch

object CsvLoader {

  private val numericalFeatureString = "n"
  private val categoricalFeatureString = "c"

  /** Loads a csv dataset with 2 header lines (feature names and types). */
  def loadCsvDataset(filePath: String, naString: String = "NA"): DenseMatrix[Double] = {
    val datasetFile = new File(filePath)
    val reader = CSVReader.open(datasetFile)

    val (featureNames, featureTypes) = inferHeaderLines(reader)
    val labelEncoder = inferLabelEncoder(datasetFile, naString, featureNames, featureTypes)

    val data = reader.toStream.map(rowValues => rowValues.zipWithIndex.map { case (value, colIndex) =>
      value match {
        case x if x == naString => Double.NaN
        case _ if featureTypes(colIndex) == numericalFeatureString =>
          (nonFatalCatch opt value.toDouble)
            .fold(throw new IllegalArgumentException(
              s"Numerical feature ${featureNames(colIndex)} contains non-numerical values"))(identity)
        case _ => labelEncoder.encode(featureNames(colIndex), value)
      }
    })

    val dataset = DenseMatrix(data:_*)
    reader.close()
    dataset
  }

  private def inferHeaderLines(reader: CSVReader): (Array[String], Array[String]) = {
    val featureNames = reader.readNext
      .fold(throw new IllegalArgumentException("File has a missing header line: feature names"))(_.toArray)

    val featureTypes = reader.readNext
      .fold(throw new IllegalArgumentException("File has a missing header line: feature types"))(_.toArray)

    require(
      featureTypes.forall(x => x == numericalFeatureString || x == categoricalFeatureString),
      "File contains invalid feature type encoding (second header line)")
    (featureNames, featureTypes)
  }

  private def inferLabelEncoder(datasetFile: File,
                                naString: String,
                                featureNames: Array[String],
                                featureTypes: Array[String]): LabelEncoder = {
    val reader = CSVReader.open(datasetFile)
    // skip the two header lines
    reader.readNext
    reader.readNext

    val uniqueValuesInitial: Map[String, Set[String]] = featureNames
      .zipWithIndex
      .filter { case (_, index) => featureTypes(index) == categoricalFeatureString }
      .map { case (name, _) => name -> Set[String]() }
      .toMap

    val encoder = reader.toStream.foldLeft(uniqueValuesInitial) { case (currentUniqueValuesRow, rowValues) =>
      rowValues.zipWithIndex.foldLeft(currentUniqueValuesRow) { case (currentUniqueValuesCol, (value, colIndex)) =>
        value match {
          case x if x == naString => currentUniqueValuesCol
          case _ if featureTypes(colIndex) == categoricalFeatureString =>
            val update = currentUniqueValuesCol(featureNames(colIndex)) + value
            currentUniqueValuesCol.updated(featureNames(colIndex), update)
          case _ => currentUniqueValuesCol
        }
      }
    }.mapValues(_.zipWithIndex.toMap.mapValues(_.toDouble))

    reader.close()
    new LabelEncoder(encoder)
  }

  private class LabelEncoder(private val encoder: Map[String, Map[String, Double]]) {
    def encode(featureName: String, value: String): Double = encoder(featureName)(value)
  }
}
