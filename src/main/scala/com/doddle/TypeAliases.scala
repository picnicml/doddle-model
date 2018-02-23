package com.doddle

import breeze.linalg.{DenseMatrix, DenseVector}

object TypeAliases {

  type IntVector = DenseVector[Int]
  type RealVector = DenseVector[Double]
  type RealMatrix = DenseMatrix[Double]
}
