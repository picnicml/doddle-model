package com.picnicml.doddlemodel.base

import java.io.Serializable

import com.picnicml.doddlemodel.data.{Dataset, Features, Target}

abstract class Transformer[A <: Transformer[A]] extends Estimator[A] {
  this: Serializable =>

  def transform(x: Features, y: Target): Dataset = {
    require(this.isFitted, "Called transform on a transformer that is not fitted yet")
    this.transformSafe(x, y)
  }

  /** A function that is guaranteed to be called on a fitted transformer. */
  protected def transformSafe(x: Features, y: Target): Dataset
}
