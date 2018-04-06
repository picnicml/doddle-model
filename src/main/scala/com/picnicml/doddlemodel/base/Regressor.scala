package com.picnicml.doddlemodel.base

import java.io.Serializable

import com.picnicml.doddlemodel.data.{Features, Target}

abstract class Regressor[A <: Regressor[A]] extends Predictor[A] {
  this: Serializable =>

  def fit(x: Features, y: Target): A
}
