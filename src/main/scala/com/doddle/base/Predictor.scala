package com.doddle.base

import breeze.linalg.{DenseMatrix, DenseVector}

abstract class Predictor[A, B] extends Estimator {

  def fit(x: DenseMatrix[A], y: DenseVector[B]): Predictor[A, B]
  def predict(x: DenseMatrix[A]): DenseVector[B]
}
