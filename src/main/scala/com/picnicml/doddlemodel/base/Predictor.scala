package com.picnicml.doddlemodel.base

import com.picnicml.doddlemodel.data.Types.{Features, Target}

abstract class Predictor extends Estimator {

  def fit(x: Features, y: Target): Predictor
  def predict(x: Features): Target
}
