package com.picnicml.doddlemodel.linear

import java.io.Serializable

import breeze.linalg.DenseVector
import com.picnicml.doddlemodel.base.Regressor
import com.picnicml.doddlemodel.data.{Features, Target}

trait LinearRegressor[A <: LinearRegressor[A]] extends Regressor[A] with LinearModel[A] {
  this: Serializable =>

  override protected def fitSafe(x: Features, y: Target): A =
    this.copy(w = this.maximumLikelihood(this.xWithBiasTerm(x), y, DenseVector.zeros[Double](x.cols + 1)))
}
