package com.doddlemodel.linear

import com.doddlemodel.base.{Predictor, Regressor}
import com.doddlemodel.data.Types.{Features, RealVector, Target}

/** An immutable multiple linear regression model with ridge regularization.
  *
  * @param lambda L2 regularization strength, must be positive, 0 means no regularization
  *
  * Examples:
  * val model = LinearRegression()
  * val model = LinearRegression(lambda = 1.5)
  */
class LinearRegression private (val lambda: Double, protected val w: Option[RealVector])
  extends Regressor with GeneralizedLinearModel[Double] {

  override protected def newInstance(w: RealVector): Predictor[Double] =
    new LinearRegression(this.lambda, Some(w))

  override protected[linear] def meanFunction(latent: RealVector): Target[Double] =
    latent

  override protected[linear] def loss(w: RealVector, x: Features, y: Target[Double]): Double = {
    val d = y - this.predict(w, x)
    .5 * ((d.t * d) / x.rows.toDouble + this.lambda * (w(1 to -1).t * w(1 to -1)))
  }

  override protected[linear] def lossGrad(w: RealVector, x: Features, y: Target[Double]): RealVector = {
    val grad = ((y - this.predict(w, x)).t * x).t / (-x.rows.toDouble)
    grad(1 to -1) += this.lambda * w(1 to -1)
    grad
  }
}

object LinearRegression {

  def apply(): LinearRegression = new LinearRegression(lambda = 0, None)

  def apply(lambda: Double): LinearRegression = {
    require(lambda >= 0, "L2 regularization strength must be positive")
    new LinearRegression(lambda, None)
  }
}
