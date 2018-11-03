package com.picnicml.doddlemodel.linear.typeclasses

import breeze.linalg.DenseVector
import com.picnicml.doddlemodel.data.{Features, Target}
import com.picnicml.doddlemodel.typeclasses.Regressor

trait LinearRegressorNew[A] extends LinearModelNew[A] with Regressor[A] {

  override protected def fitSafe(linearRegressor: A, x: Features, y: Target): A = {
    val wInitial = DenseVector.zeros[Double](x.cols + 1)
    copy(linearRegressor, w = maximumLikelihood(linearRegressor, xWithBiasTerm(x), y, wInitial))
  }
}
