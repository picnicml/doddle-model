package com.picnicml.doddlemodel.base

import java.io.Serializable

import com.picnicml.doddlemodel.data.{Features, Target}

abstract class Predictor extends Estimator {
  this: Serializable =>

  def fit(x: Features, y: Target): Predictor
  def predict(x: Features): Target
}
