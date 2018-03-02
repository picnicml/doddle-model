package com.doddlemodel.base

import com.doddlemodel.data.Types.{Features, Target}

abstract class Predictor[A] extends Estimator {

  def fit(x: Features, y: Target[A]): Predictor[A]
  def predict(x: Features): Target[A]
}
