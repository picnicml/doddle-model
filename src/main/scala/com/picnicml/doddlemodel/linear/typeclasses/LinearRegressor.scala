package com.picnicml.doddlemodel.linear.typeclasses

import breeze.linalg.DenseVector
import com.picnicml.doddlemodel.data.{Features, Target}
import com.picnicml.doddlemodel.typeclasses.Regressor

trait LinearRegressor[A] extends LinearModel[A] with Regressor[A] {

  override protected def fitSafe(model: A, x: Features, y: Target): A = {
    val wInitial = DenseVector.zeros[Double](x.cols + 1)
    copy(model, w = maximumLikelihood(model, xWithBiasTerm(x), y, wInitial))
  }
}
