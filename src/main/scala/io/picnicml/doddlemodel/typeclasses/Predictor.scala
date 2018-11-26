package io.picnicml.doddlemodel.typeclasses

import io.picnicml.doddlemodel.data.{Features, Target}

trait Predictor[A] extends Estimator[A] {

  def fit(model: A, x: Features, y: Target): A

  def predict(model: A, x: Features): Target = {
    require(isFitted(model), "Called predict on a model that is not trained yet")
    predictSafe(model, x)
  }

  /** A function that is guaranteed to be called on a fitted model. */
  protected def predictSafe(model: A, x: Features): Target
}
