package com.doddle.data

import java.io.File

import breeze.linalg.csvread
import com.doddle.TypeAliases.{RealMatrix, RealVector}

object DataLoaders {

  private val datasetsDir = "datasets"

  def loadBostonDataset: (RealMatrix, RealVector) = {
    val data = csvread(new File(getDatasetPath("boston_housing_prices.csv")), skipLines = 1)
    (data(::, 0 to -2), data(::, -1))
  }

  private def getDatasetPath(dataFileName: String): String =
    getClass.getResource(s"/$datasetsDir/$dataFileName").getPath
}
