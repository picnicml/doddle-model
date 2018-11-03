package com.picnicml.doddlemodel.typeclasses

import com.picnicml.doddlemodel.data.{Features, Target}

trait Predictor[A] extends Estimator[A] {

  def fit(predictor: A, x: Features, y: Target): A

  def predict(predictor: A, x: Features): Target = {
    require(isFitted(predictor), "Called predict on a model that is not trained yet")
    predictSafe(predictor, x)
  }

  /** A function that is guaranteed to be called on a fitted model. */
  protected def predictSafe(predictor: A, x: Features): Target
}
