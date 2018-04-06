package com.picnicml.doddlemodel.base

import java.io.Serializable

import com.picnicml.doddlemodel.data.{Features, Target}

abstract class Predictor[A <: Predictor[A]] extends Estimator {
  this: Serializable =>

  def fit(x: Features, y: Target): A
  def predict(x: Features): Target
}
