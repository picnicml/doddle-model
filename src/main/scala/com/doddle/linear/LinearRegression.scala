package com.doddle.linear

import breeze.linalg.{DenseMatrix, DenseVector}
import com.doddle.base.{Predictor, Regressor}

class LinearRegression extends Regressor {

  override def fit(x: DenseMatrix[Double], y: DenseVector[Double]): Predictor[Double, Double] = {
    // todo
    this
  }

  override def predict(x: DenseMatrix[Double]): DenseVector[Double] = {
    // todo
    DenseVector.zeros[Double](5)
  }
}
