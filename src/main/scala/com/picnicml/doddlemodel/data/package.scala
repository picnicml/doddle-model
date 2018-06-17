package com.picnicml.doddlemodel

import java.io.File

import breeze.linalg.{DenseMatrix, DenseVector}
import com.github.tototoshi.csv.CSVReader

import scala.util.Random

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

  def shuffleDataset(x: Features, y: Target)(implicit rand: Random = new Random()): Dataset = {
    val shuffleIndices = rand.shuffle[Int, IndexedSeq](0 until y.length)
    (x(shuffleIndices, ::).toDenseMatrix, y(shuffleIndices).toDenseVector)
  }

  def splitDataset(x: Features, y: Target, proportionTrain: Double = 0.5): (Features, Target, Features, Target) = {
    require(proportionTrain > 0.0 && proportionTrain < 1.0, "proportionTrain must be between 0 and 1")
    val numTrain = (proportionTrain * x.rows.toDouble).toInt
    require(numTrain > 0 && numTrain < x.rows, "the value of proportionTrain is either too high or too low")

    val trIndices = 0 until numTrain
    val teIndices = numTrain until x.rows
    (x(trIndices, ::), y(trIndices), x(teIndices, ::), y(teIndices))
  }
}
