package io.picnicml.doddlemodel.data

import breeze.linalg.DenseMatrix
import io.picnicml.doddlemodel.data.CsvLoader.loadCsvDataset

object ResourceDatasetLoaders {

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

  private[data] def loadDummyCsvReadingDataset: Dataset = {
    val data = loadDatasetFromResources("dummy_csv_reading")
    (data(::, 0 to -2), data(::, -1))
  }

  private def loadDatasetFromResources(datasetName: String): DenseMatrix[Double] = {
    val path = getClass.getResource(s"/$datasetsDir/$datasetName.csv").getPath
    loadCsvDataset(path)
  }
}
