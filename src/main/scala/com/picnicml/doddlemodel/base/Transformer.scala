package com.picnicml.doddlemodel.base

import java.io.Serializable

import com.picnicml.doddlemodel.data.Features

abstract class Transformer[A <: Transformer[A]] extends Estimator[A] {
  this: Serializable =>

  def transform(x: Features): Features = {
    require(this.isFitted, "Called transform on a transformer that is not fitted yet")
    this.transformSafe(x)
  }

  /** A function that is guaranteed to be called on a fitted transformer. */
  protected def transformSafe(x: Features): Features
}
