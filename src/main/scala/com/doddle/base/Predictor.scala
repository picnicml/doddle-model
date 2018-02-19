package com.doddle.base

import breeze.linalg.DenseVector
import com.doddle.Types.RealMatrix

abstract class Predictor[A] extends Estimator {

  def fit(x: RealMatrix, y: DenseVector[A]): Unit
  def predict(x: RealMatrix): DenseVector[A]
}
