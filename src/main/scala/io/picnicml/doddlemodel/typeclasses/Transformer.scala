package io.picnicml.doddlemodel.typeclasses

import io.picnicml.doddlemodel.data.Feature.FeatureIndex
import io.picnicml.doddlemodel.data.Features

trait Transformer[A] extends Estimator[A] {

  def fit(model: A, x: Features): A

  /** A function that returns the feature index of domain after transformation. */
  def featureIndex(model: A): FeatureIndex = {
    require(isFitted(model), "Requested modified feature index on a model that is not fitted yet")
    featureIndexSafe(model)
  }

  def transform(model: A, x: Features): Features = {
    require(isFitted(model), "Called transform on a transformer that is not fitted yet")
    transformSafe(model, x)
  }

  /* Functions that are guaranteed to be called on a fitted transformer. */
  protected def featureIndexSafe(model: A): FeatureIndex

  protected def transformSafe(model: A, x: Features): Features
}
