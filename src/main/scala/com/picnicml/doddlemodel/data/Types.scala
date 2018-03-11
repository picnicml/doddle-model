package com.picnicml.doddlemodel.data

import breeze.linalg.{DenseMatrix, DenseVector}

object Types {

  type Features = DenseMatrix[Double]
  type Target = DenseVector[Double]
  type Simplex = DenseMatrix[Double]
  type RealVector = DenseVector[Double]
}
