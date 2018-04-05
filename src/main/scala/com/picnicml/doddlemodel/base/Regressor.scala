package com.picnicml.doddlemodel.base

import java.io.Serializable

import com.picnicml.doddlemodel.data.{Features, Target}

abstract class Regressor extends Predictor {
  this: Serializable =>

  def fit(x: Features, y: Target): Regressor
}
