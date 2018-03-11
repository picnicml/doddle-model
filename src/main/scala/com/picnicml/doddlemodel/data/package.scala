package com.picnicml.doddlemodel

import breeze.linalg.{DenseMatrix, DenseVector}

package object data {

  type Features = DenseMatrix[Double]
  type Target = DenseVector[Double]
  type Simplex = DenseMatrix[Double]
  type RealVector = DenseVector[Double]
  type Dataset = (Features, Target)

  def loadBostonDataset: Dataset = DataLoaders.loadBostonDataset
  def loadBreastCancerDataset: Dataset = DataLoaders.loadBreastCancerDataset
}
