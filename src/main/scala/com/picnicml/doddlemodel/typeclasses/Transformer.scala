package com.picnicml.doddlemodel.typeclasses

import com.picnicml.doddlemodel.data.Features

trait Transformer[A] extends Estimator[A] {

  def fit(transformer: A, x: Features): A

  def transform(transformer: A, x: Features): Features = {
    require(isFitted(transformer), "Called transform on a transformer that is not fitted yet")
    transformSafe(transformer, x)
  }

  /** A function that is guaranteed to be called on a fitted transformer. */
  protected def transformSafe(transformer: A, x: Features): Features
}
