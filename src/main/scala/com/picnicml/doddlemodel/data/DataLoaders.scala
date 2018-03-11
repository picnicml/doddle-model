package com.picnicml.doddlemodel.data

import java.io.File

import breeze.linalg.csvread

object DataLoaders {

  private val datasetsDir = "datasets"

  def loadBostonDataset: Dataset = {
    val data = csvread(new File(getDatasetPath("boston_housing_prices.csv")), skipLines = 1)
    (data(::, 0 to -2), data(::, -1))
  }

  def loadBreastCancerDataset: Dataset = {
    val data = csvread(new File(getDatasetPath("breast_cancer.csv")), skipLines = 1)
    (data(::, 0 to -2), data(::, -1))
  }

  private def getDatasetPath(dataFileName: String): String =
    getClass.getResource(s"/$datasetsDir/$dataFileName").getPath
}
