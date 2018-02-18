package com.doddle.base

import breeze.linalg.DenseMatrix

abstract class Transformer[A] extends Estimator {

  def fit(x: DenseMatrix[A]): Transformer[A]
  def transform(x: DenseMatrix[A]): DenseMatrix[A]
}
