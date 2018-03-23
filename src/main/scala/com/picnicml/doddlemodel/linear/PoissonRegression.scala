package com.picnicml.doddlemodel.linear

import breeze.linalg.sum
import breeze.numerics.{exp, floor, log}
import com.picnicml.doddlemodel.Regularization.{ridgeLoss, ridgeLossGrad}
import com.picnicml.doddlemodel.base.Regressor
import com.picnicml.doddlemodel.data.{Features, RealVector, Target}

/** An immutable multiple Poisson regression model with ridge regularization.
  *
  * @param lambda L2 regularization strength, must be positive, 0 means no regularization
  *
  * Examples:
  * val model = PoissonRegression()
  * val model = PoissonRegression(lambda = 1.5)
  */
class PoissonRegression private (val lambda: Double, protected val w: Option[RealVector])
  extends Regressor with LinearModel with LinearRegressor {

  override protected def copy(w: RealVector): Regressor =
    new PoissonRegression(this.lambda, Some(w))

  override protected def predict(w: RealVector, x: Features): Target =
    floor(this.predictMean(w, x))

  /**
    * A function that returns the mean of the Poisson distribution, similar to
    * predictProba(...) in com.picnicml.doddlemodel.linear.LogisticRegression.
    */
  def predictMean(w: RealVector, x: Features): Target = exp(x * w)

  override protected[linear] def loss(w: RealVector, x: Features, y: Target): Double = {
    val yMeanPred = this.predictMean(w, x)
    sum(y * log(yMeanPred) - yMeanPred) / (-x.rows.toDouble) + ridgeLoss(w(1 to -1), this.lambda)
  }

  override protected[linear] def lossGrad(w: RealVector, x: Features, y: Target): RealVector = {
    val grad = ((this.predictMean(w, x) - y).t * x).t / x.rows.toDouble
    grad(1 to -1) += ridgeLossGrad(w(1 to -1), lambda)
    grad
  }
}

object PoissonRegression {

  def apply(): PoissonRegression = new PoissonRegression(0, None)

  def apply(lambda: Double): PoissonRegression = {
    require(lambda >= 0, "L2 regularization strength must be positive")
    new PoissonRegression(lambda, None)
  }
}
