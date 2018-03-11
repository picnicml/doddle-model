package com.picnicml.doddlemodel.linear

import com.picnicml.doddlemodel.base.Regressor
import com.picnicml.doddlemodel.data.{Features, RealVector, Target}

/** An immutable multiple linear regression model with ridge regularization.
  *
  * @param lambda L2 regularization strength, must be positive, 0 means no regularization
  *
  * Examples:
  * val model = LinearRegression()
  * val model = LinearRegression(lambda = 1.5)
  */
class LinearRegression private (val lambda: Double, protected val w: Option[RealVector])
  extends Regressor with LinearModel with LinearRegressor {

  override protected def copy(w: RealVector): Regressor =
    new LinearRegression(this.lambda, Some(w))

  override protected def predict(w: RealVector, x: Features): Target = x * w

  override protected[linear] def loss(w: RealVector, x: Features, y: Target): Double = {
    val d = y - this.predict(w, x)
    .5 * ((d.t * d) / x.rows.toDouble + this.lambda * (w(1 to -1).t * w(1 to -1)))
  }

  override protected[linear] def lossGrad(w: RealVector, x: Features, y: Target): RealVector = {
    val grad = ((y - this.predict(w, x)).t * x).t / (-x.rows.toDouble)
    grad(1 to -1) += this.lambda * w(1 to -1)
    grad
  }
}

object LinearRegression {

  def apply(): LinearRegression = new LinearRegression(0, None)

  def apply(lambda: Double): LinearRegression = {
    require(lambda >= 0, "L2 regularization strength must be positive")
    new LinearRegression(lambda, None)
  }
}
