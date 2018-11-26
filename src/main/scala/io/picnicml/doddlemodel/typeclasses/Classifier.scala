package io.picnicml.doddlemodel.typeclasses

import io.picnicml.doddlemodel.data.{Features, Simplex, Target, numberOfTargetClasses}

trait Classifier[A] extends Predictor[A] {

  def numClasses(model: A): Option[Int]

  override def fit(model: A, x: Features, y: Target): A = {
    require(!isFitted(model), "Called fit on a model that is already trained")
    fitSafe(copy(model, numClasses = numberOfTargetClasses(y)), x, y)
  }

  /** A function that creates a new classifier with numClasses set. */
  protected[doddlemodel] def copy(model: A, numClasses: Int): A

  /**
    * A function that is guaranteed to receive an appropriate target variable when called. Additionally,
    * the object is guaranteed not to be fitted.
    */
  protected def fitSafe(model: A, x: Features, y: Target): A

  def predictProba(model: A, x: Features): Simplex = {
    require(isFitted(model), "Called predictProba on a model that is not trained yet")
    predictProbaSafe(model, x)
  }

  /** A function that is guaranteed to be called on a fitted model. */
  protected def predictProbaSafe(model: A, x: Features): Simplex
}
