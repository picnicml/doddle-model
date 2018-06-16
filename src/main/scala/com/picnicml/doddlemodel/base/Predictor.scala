package com.picnicml.doddlemodel.base

import java.io.Serializable

import com.picnicml.doddlemodel.data.{Features, Target}

abstract class Predictor[A <: Predictor[A]] extends Estimator[A] {
  this: A with Serializable =>

  def predict(x: Features): Target = {
    require(this.isFitted, "Called predict on a model that is not trained yet")
    this.predictSafe(x)
  }

  /** A function that is guaranteed to be called on a fitted model. */
  protected def predictSafe(x: Features): Target
}
