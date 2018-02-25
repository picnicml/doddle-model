package com.doddle.base

import com.doddle.data.DataTypes.{Features, Target}

abstract class Predictor[A] extends Estimator {

  def fit(x: Features, y: Target[A]): Predictor[A]
  def predict(x: Features): Target[A]
}
