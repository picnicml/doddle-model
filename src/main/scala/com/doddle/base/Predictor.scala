package com.doddle.base

import breeze.linalg.DenseVector
import com.doddle.TypeAliases.RealMatrix

abstract class Predictor[A] extends Estimator {

  def fit(x: RealMatrix, y: DenseVector[A]): Unit
  def predict(x: RealMatrix): DenseVector[A]
}
