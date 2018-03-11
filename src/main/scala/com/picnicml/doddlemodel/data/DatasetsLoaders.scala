package com.picnicml.doddlemodel.data

import java.io.InputStreamReader

import breeze.io.CSVReader
import breeze.linalg.DenseMatrix

object DatasetsLoaders {

  private val datasetsDir = "datasets"

  def loadBostonDataset: Dataset = {
    val data = loadDataset("boston_housing_prices")
    (data(::, 0 to -2), data(::, -1))
  }

  def loadBreastCancerDataset: Dataset = {
    val data = loadDataset("breast_cancer")
    (data(::, 0 to -2), data(::, -1))
  }

  private def loadDataset(datasetName: String): DenseMatrix[Double] = {
    val input = new InputStreamReader(getClass.getResourceAsStream(s"/$datasetsDir/$datasetName.csv"))
    var matrix = CSVReader.read(input, separator = ',', quote = '"', escape = '\\', skipLines = 1)
    matrix = matrix.takeWhile(line => line.nonEmpty && line.head.nonEmpty)
    input.close()
    DenseMatrix.tabulate(matrix.length, matrix.head.length)((i, j) => matrix(i)(j).toDouble)
  }
}
