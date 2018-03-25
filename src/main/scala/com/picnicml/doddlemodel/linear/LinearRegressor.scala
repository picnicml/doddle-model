package com.picnicml.doddlemodel.linear

import breeze.linalg.DenseVector
import com.picnicml.doddlemodel.base.Regressor
import com.picnicml.doddlemodel.data.{Features, RealVector, Target}

trait LinearRegressor extends Regressor with LinearModel {

  /** A function that creates a new regressor with model parameters w. */
  protected def copy(w: RealVector): Regressor

  /** A function that checks whether the target variable contains valid data. */
  protected def checkTargetVarRequirement(y: Target): Unit

  override def fit(x: Features, y: Target): Regressor = {
    require(!this.isFitted, "Called fit on a model that is already trained")
    this.checkTargetVarRequirement(y)

    val w = this.maximumLikelihood(this.xWithBiasTerm(x), y, DenseVector.zeros[Double](x.cols + 1))
    this.copy(w)
  }
}
