package com.picnicml.doddlemodel.base

import java.io.Serializable

import com.picnicml.doddlemodel.data.{Features, Target}

abstract class Regressor[A <: Regressor[A]] extends Predictor[A] {
  this: A with Serializable =>

  override def fit(x: Features, y: Target): A = {
    require(!this.isFitted, "Called fit on a model that is already trained")
    require(this.targetVariableAppropriate(y), "Target variable contains invalid data")
    this.copy.fitSafe(x, y)
  }

  /** A function that creates an identical regressor. */
  protected def copy: A

  /** A function that checks whether the target variable contains valid data. */
  protected def targetVariableAppropriate(y: Target): Boolean

  /**
    * A function that is guaranteed to receive an appropriate target variable when called. Additionally,
    * the object is guaranteed not to be fitted.
    */
  protected def fitSafe(x: Features, y: Target): A
}
