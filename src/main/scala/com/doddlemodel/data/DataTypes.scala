package com.doddlemodel.data

import breeze.linalg.{DenseMatrix, DenseVector}

object DataTypes {

  type Features = DenseMatrix[Double]
  type Target[A] = DenseVector[A]
  type Simplex = DenseMatrix[Double]
  type RealVector = DenseVector[Double]
}
