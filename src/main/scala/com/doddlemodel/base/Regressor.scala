package com.doddlemodel.base

import com.doddlemodel.data.Types.{Features, Target}

abstract class Regressor extends Predictor {

  def fit(x: Features, y: Target): Regressor
}
