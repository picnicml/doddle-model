package com.picnicml.doddlemodel.linear

import com.picnicml.doddlemodel.base.Regressor
import com.picnicml.doddlemodel.data.{Features, RealVector, Target}

trait LinearRegressor {
  this: LinearModel with Regressor =>

  /** A function that creates a new regressor with model parameters w. */
  protected def copy(w: RealVector): Regressor

  override def fit(x: Features, y: Target): Regressor = {
    require(!this.isTrained, "Called fit on a model that is already trained")
    this.copy(this.findModelParameters(x, y))
  }
}
