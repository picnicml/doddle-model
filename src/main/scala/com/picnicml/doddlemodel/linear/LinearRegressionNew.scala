package com.picnicml.doddlemodel.linear

import com.picnicml.doddlemodel.data.{Features, RealVector, Target}
import com.picnicml.doddlemodel.linear.typeclasses.LinearRegressorNew

case class LinearRegressionNew private(lambda: Double, private val w: Option[RealVector]) {
  private var yPredCache: Target = _
}

object LinearRegressionNew {

  def apply(): LinearRegressionNew = new LinearRegressionNew(0, None)

  def apply(lambda: Double): LinearRegressionNew = {
    require(lambda >= 0, "L2 regularization strength must be positive")
    new LinearRegressionNew(lambda, None)
  }

  private val wSlice: Range.Inclusive = 1 to -1

  implicit lazy val linearRegression: LinearRegressorNew[LinearRegressionNew] = new LinearRegressorNew[LinearRegressionNew] {

    override protected def w(model: LinearRegressionNew): Option[RealVector] = model.w

    override protected def copy(regressor: LinearRegressionNew): LinearRegressionNew =
      new LinearRegressionNew(regressor.lambda, regressor.w)

    override protected def copy(model: LinearRegressionNew, w: RealVector): LinearRegressionNew =
      new LinearRegressionNew(model.lambda, Some(w))

    @inline override protected def targetVariableAppropriate(y: Target): Boolean = true

    override protected def predict(w: RealVector, x: Features): Target = x * w

    override protected[linear] def loss(model: LinearRegressionNew, w: RealVector, x: Features, y: Target): Double = {
      model.yPredCache = this.predict(w, x)
      val d = y - model.yPredCache
      .5 * (((d.t * d) / x.rows.toDouble) + model.lambda * (w(wSlice).t * w(wSlice)))
    }

    override protected[linear] def lossGrad(model: LinearRegressionNew,
                                            w: RealVector, x: Features, y: Target): RealVector = {
      val grad = ((y - model.yPredCache).t * x).t / (-x.rows.toDouble)
      grad(wSlice) += model.lambda * w(wSlice)
      grad
    }
  }
}
