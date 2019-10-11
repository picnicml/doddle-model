package io.picnicml.doddlemodel.linear.typeclasses

import breeze.linalg.DenseVector
import io.picnicml.doddlemodel.data.{Features, Target}
import io.picnicml.doddlemodel.typeclasses.Regressor

trait LinearRegressor[A] extends LinearModel[A] with Regressor[A] {

  override protected def fitSafe(model: A, x: Features, y: Target): A = {
    val wInitial = DenseVector.zeros[Float](x.cols + 1)
    copy(model, w = maximumLikelihood(model, xWithBiasTerm(x), y, wInitial))
  }
}
