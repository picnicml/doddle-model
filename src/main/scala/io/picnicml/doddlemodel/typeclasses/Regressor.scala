package io.picnicml.doddlemodel.typeclasses

import io.picnicml.doddlemodel.data.{Features, Target}

trait Regressor[A] extends Predictor[A] {

  override def fit(model: A, x: Features, y: Target): A = {
    require(!isFitted(model), "Called fit on a model that is already trained")
    require(targetVariableAppropriate(y), "Target variable contains invalid data")
    fitSafe(copy(model), x, y)
  }

  /** A function that creates an identical regressor. */
  protected def copy(model: A): A

  /** A function that checks whether the target variable contains valid data. */
  protected def targetVariableAppropriate(y: Target): Boolean

  /**
    * A function that is guaranteed to receive an appropriate target variable when called. Additionally,
    * the object is guaranteed not to be fitted.
    */
  protected def fitSafe(model: A, x: Features, y: Target): A
}
