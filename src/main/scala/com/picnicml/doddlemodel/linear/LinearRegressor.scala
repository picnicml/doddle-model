package com.picnicml.doddlemodel.linear

import breeze.linalg.DenseVector
import com.picnicml.doddlemodel.data.{Features, Target}
import com.picnicml.doddlemodel.typeclasses.Regressor

trait LinearRegressor[A] extends Regressor[A] with LinearModel[A] {

  final override protected def fitSafe(regressor: A, x: Features, y: Target): A = {
    val w = this.maximumLikelihood(regressor, this.xWithBiasTerm(x), y, DenseVector.zeros[Double](x.cols + 1))
    this.copy(regressor, w)
  }
}
