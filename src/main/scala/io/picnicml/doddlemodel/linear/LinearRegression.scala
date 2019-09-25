package io.picnicml.doddlemodel.linear

import cats.syntax.option._
import io.picnicml.doddlemodel.data.{Features, RealVector, Target}
import io.picnicml.doddlemodel.linear.typeclasses.LinearRegressor

/** An immutable multiple linear regression model with ridge regularization.
  *
  * @param lambda L2 regularization strength, must be positive, 0 means no regularization
  *
  * Examples:
  * val model = LinearRegression()
  * val model = LinearRegression(lambda = 1.5)
  */
case class LinearRegression private (lambda: Double, private val w: Option[RealVector]) {
  private var yPredCache: Target = _
}

object LinearRegression {

  def apply(lambda: Double = 0.0): LinearRegression = {
    require(lambda >= 0.0, "L2 regularization strength must be non-negative")
    LinearRegression(lambda, none)
  }

  private val wSlice: Range.Inclusive = 1 to -1

  implicit lazy val ev: LinearRegressor[LinearRegression] = new LinearRegressor[LinearRegression] {

    override protected def w(model: LinearRegression): Option[RealVector] = model.w

    override protected def copy(model: LinearRegression): LinearRegression =
      model.copy()

    override protected def copy(model: LinearRegression, w: RealVector): LinearRegression =
      model.copy(w = w.some)

    @inline override protected def targetVariableAppropriate(y: Target): Boolean = true

    override protected def predictStateless(model: LinearRegression, w: RealVector, x: Features): Target = x * w

    override protected[linear] def lossStateless(model: LinearRegression,
                                                 w: RealVector, x: Features, y: Target): Double = {
      model.yPredCache = predictStateless(model, w, x)
      val d = y - model.yPredCache
      .5 * (((d.t * d) / x.rows.toDouble) + model.lambda * (w(wSlice).t * w(wSlice)))
    }

    override protected[linear] def lossGradStateless(model: LinearRegression,
                                                     w: RealVector, x: Features, y: Target): RealVector = {
      val grad = ((y - model.yPredCache).t * x).t / (-x.rows.toDouble)
      grad(wSlice) += model.lambda * w(wSlice)
      grad
    }
  }
}
