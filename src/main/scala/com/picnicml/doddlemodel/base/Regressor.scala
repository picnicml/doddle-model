package com.picnicml.doddlemodel.base

import com.picnicml.doddlemodel.data.{Features, Target}

abstract class Regressor extends Predictor {

  def fit(x: Features, y: Target): Regressor
}
