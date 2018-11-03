package com.picnicml.doddlemodel.typeclasses

import com.picnicml.doddlemodel.data.{Features, Target}

trait Regressor[A] extends Predictor[A] {

  override def fit(regressor: A, x: Features, y: Target): A = {
    require(!isFitted(regressor), "Called fit on a model that is already trained")
    require(targetVariableAppropriate(regressor, y), "Target variable contains invalid data")
    fitSafe(copy(regressor), x, y)
  }

  /** A function that creates an identical regressor. */
  protected def copy(regressor: A): A

  /** A function that checks whether the target variable contains valid data. */
  protected def targetVariableAppropriate(regressor: A, y: Target): Boolean

  /**
    * A function that is guaranteed to receive an appropriate target variable when called. Additionally,
    * the object is guaranteed not to be fitted.
    */
  protected def fitSafe(regressor: A, x: Features, y: Target): A
}
