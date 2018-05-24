package com.picnicml.doddlemodel

import java.io.File

import breeze.linalg.{DenseMatrix, DenseVector}
import com.github.tototoshi.csv.CSVReader

package object data {

  type Features = DenseMatrix[Double]
  type Target = DenseVector[Double]
  type Simplex = DenseMatrix[Double]
  type RealVector = DenseVector[Double]
  type Dataset = (Features, Target)

  def loadBostonDataset: Dataset = DatasetsLoaders.loadBostonDataset
  def loadBreastCancerDataset: Dataset = DatasetsLoaders.loadBreastCancerDataset
  def loadIrisDataset: Dataset = DatasetsLoaders.loadIrisDataset
  def loadHighSchoolTestDataset: Dataset = DatasetsLoaders.loadHighSchoolTestDataset

  def loadCsvDataset(filePath: String, headerLine: Boolean = true): DenseMatrix[Double] = {
    val reader = CSVReader.open(new File(filePath))
    if (headerLine) reader.readNext()
    val data = DenseMatrix(reader.toStream.map(_.map(_.toDouble).toArray):_*)
    reader.close()
    data
  }
}
