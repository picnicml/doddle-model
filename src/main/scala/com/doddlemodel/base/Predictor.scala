package com.doddlemodel.base

import com.doddlemodel.data.Types.{Features, Target}

abstract class Predictor extends Estimator {

  def fit(x: Features, y: Target): Predictor
  def predict(x: Features): Target
}
