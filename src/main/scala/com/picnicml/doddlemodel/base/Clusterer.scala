package com.picnicml.doddlemodel.base

import java.io.Serializable

import com.picnicml.doddlemodel.data.{Features, Target}

abstract class Clusterer[A <: Clusterer[A]] extends Estimator {
  this: A with Serializable =>

  /** A function that creates an identical clusterer. */
  protected def copy: A

  def predict(x: Features): Target = {
    require(this.isFitted, "Called predict on a model that is not trained yet")
    this.predictSafe(x)
  }

  /** A function that is guaranteed to be called on a fitted model. */
  protected def predictSafe(x: Features): Target
}
