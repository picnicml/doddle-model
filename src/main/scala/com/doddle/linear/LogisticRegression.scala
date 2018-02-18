package com.doddle.linear

import breeze.linalg.{DenseMatrix, DenseVector}
import com.doddle.base.{Classifier, Predictor}

class LogisticRegression extends Classifier {

  override def fit(x: DenseMatrix[Double], y: DenseVector[Int]): Predictor[Double, Int] = {
    // todo
    this
  }

  override def predict(x: DenseMatrix[Double]): DenseVector[Int] = {
    // todo
    DenseVector.zeros[Int](5)
  }
}
