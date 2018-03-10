package com.doddlemodel.data

import java.io.File

import breeze.linalg.{convert, csvread}
import com.doddlemodel.data.Types.{Features, Target}

object DataLoaders {

  private val datasetsDir = "datasets"

  def loadBostonDataset: (Features, Target[Double]) = {
    val data = csvread(new File(getDatasetPath("boston_housing_prices.csv")), skipLines = 1)
    (data(::, 0 to -2), data(::, -1))
  }

  def loadBreastCancerDataset: (Features, Target[Int]) = {
    val data = csvread(new File(getDatasetPath("breast_cancer.csv")), skipLines = 1)
    (data(::, 0 to -2), convert(data(::, -1), Int))
  }

  private def getDatasetPath(dataFileName: String): String =
    getClass.getResource(s"/$datasetsDir/$dataFileName").getPath
}
