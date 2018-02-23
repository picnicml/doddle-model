package com.doddle.linear

import com.doddle.Types.{RealMatrix, RealVector}
import com.doddle.base.Regressor

/** A multiple linear regression model with ridge regularization.
  *
  * @param lambda L2 regularization strength, 0 means no regularization
  *
  * Examples:
  * // initialize a model without L2 regularization
  * val model = LinearRegression()
  * // initialize a model with L2 regularization
  * val model = LinearRegression(lambda = 1.5)
  */
class LinearRegression private (val lambda: Double) extends Regressor with GeneralizedLinearModel[Double] {

  protected[linear] def meanFunction(latent: RealVector): RealVector = latent

  protected[linear] def loss(w: RealVector, x: RealMatrix, y: RealVector): Double = {
    val d = y - predict(w, x)
    .5 * ((d.t * d) / x.rows.toDouble + this.lambda * (w(1 to -1).t * w(1 to -1)))
  }

  protected[linear] def lossGrad(w: RealVector, x: RealMatrix, y: RealVector): RealVector = {
    val grad = ((y - predict(w, x)).t * x).t / (-x.rows.toDouble)
    grad(1 to -1) += this.lambda * w(1 to -1)
    grad
  }
}

object LinearRegression {
  def apply() = new LinearRegression(0)
  def apply(lambda: Double) = new LinearRegression(lambda)
}
