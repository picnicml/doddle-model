package io.picnicml.doddlemodel.typeclasses

import io.picnicml.doddlemodel.data.Features

trait Transformer[A] extends Estimator[A] {

  def fit(model: A, x: Features): A

  def transform(model: A, x: Features): Features = {
    require(isFitted(model), "Called transform on a transformer that is not fitted yet")
    transformSafe(model, x)
  }

  /** A function that is guaranteed to be called on a fitted transformer. */
  protected def transformSafe(model: A, x: Features): Features
}
