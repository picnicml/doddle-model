package com.doddlemodel.linear

import com.doddlemodel.base.Regressor
import com.doddlemodel.data.Types.{Features, RealVector, Target}

trait LinearRegressor[A] {
  this: LinearModel[A] with Regressor[A] =>

  /** A function that creates a new regressor with model parameters w. */
  protected def copy(w: RealVector): Regressor[A]

  override def fit(x: Features, y: Target[A]): Regressor[A] = {
    require(!this.isTrained, "Called fit on a model that is already trained")
    this.copy(this.findModelParameters(x, y))
  }
}
