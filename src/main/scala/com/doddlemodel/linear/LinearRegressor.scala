package com.doddlemodel.linear

import com.doddlemodel.base.Regressor
import com.doddlemodel.data.Types.{Features, RealVector, Target}

trait LinearRegressor {
  this: LinearModel with Regressor =>

  /** A function that creates a new regressor with model parameters w. */
  protected def copy(w: RealVector): Regressor

  override def fit(x: Features, y: Target): Regressor = {
    require(!this.isTrained, "Called fit on a model that is already trained")
    this.copy(this.findModelParameters(x, y))
  }
}
