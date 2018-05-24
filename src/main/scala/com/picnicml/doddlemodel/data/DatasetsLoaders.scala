package com.picnicml.doddlemodel.data

import java.io.InputStreamReader

import breeze.io.{CSVReader => BreezeCSVReader}
import breeze.linalg.DenseMatrix

object DatasetsLoaders {

  private val datasetsDir = "datasets"

  def loadBostonDataset: Dataset = {
    val data = loadDatasetFromResources("boston_housing_prices")
    (data(::, 0 to -2), data(::, -1))
  }

  def loadBreastCancerDataset: Dataset = {
    val data = loadDatasetFromResources("breast_cancer")
    (data(::, 0 to -2), data(::, -1))
  }

  def loadIrisDataset: Dataset = {
    val data = loadDatasetFromResources("iris")
    (data(::, 0 to -2), data(::, -1))
  }

  /** An artificial dataset with a Poisson target variable. */
  def loadHighSchoolTestDataset: Dataset = {
    val data = loadDatasetFromResources("high_school_test")
    (data(::, 0 to -2), data(::, -1))
  }

  private def loadDatasetFromResources(datasetName: String): DenseMatrix[Double] = {
    val input = new InputStreamReader(getClass.getResourceAsStream(s"/$datasetsDir/$datasetName.csv"))
    var matrix = BreezeCSVReader.read(input, separator = ',', quote = '"', escape = '\\', skipLines = 1)
    matrix = matrix.takeWhile(line => line.nonEmpty && line.head.nonEmpty)
    input.close()
    DenseMatrix.tabulate(matrix.length, matrix.head.length)((i, j) => matrix(i)(j).toDouble)
  }
}
