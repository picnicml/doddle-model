package com.picnicml.doddlemodel.linear

import breeze.linalg.{all, sum}
import breeze.numerics.{exp, floor, isFinite, log}
import com.picnicml.doddlemodel.data.{Features, RealVector, Target}

/** An immutable multiple Poisson regression model with ridge regularization.
  *
  * @param lambda L2 regularization strength, must be positive, 0 means no regularization
  *
  * Examples:
  * val model = PoissonRegression()
  * val model = PoissonRegression(lambda = 1.5)
  */
@SerialVersionUID(1L)
class PoissonRegression private (val lambda: Double, protected val w: Option[RealVector])
  extends LinearRegressor[PoissonRegression] with Serializable {


  override protected def copy: PoissonRegression = new PoissonRegression(this.lambda, this.w)

  override protected def copy(w: RealVector): PoissonRegression = new PoissonRegression(this.lambda, Some(w))

  override protected def targetVariableAppropriate(y: Target): Boolean = y == floor(y) && all(isFinite(y))

  override protected def predict(w: RealVector, x: Features): Target = floor(this.predictMean(w, x))

  /**
    * A function that returns the mean of the Poisson distribution, similar to
    * predictProba(...) in com.picnicml.doddlemodel.linear.LogisticRegression.
    */
  def predictMean(x: Features): Target = {
    require(this.isFitted, "Called predictMean on a model that is not trained yet")
    this.predictMean(this.w.get, x)
  }

  private def predictMean(w: RealVector, x: Features): Target = exp(x * w)

  private var yPredMeanCache: Target = _
  private val slice: Range.Inclusive = 1 to -1

  override protected[linear] def loss(w: RealVector, x: Features, y: Target): Double = {
    yPredMeanCache = this.predictMean(w, x)
    sum(y * log(yPredMeanCache) - yPredMeanCache) / (-x.rows.toDouble) +
      .5 * this.lambda * (w(slice).t * w(slice))
  }

  override protected[linear] def lossGrad(w: RealVector, x: Features, y: Target): RealVector = {
    val grad = ((yPredMeanCache - y).t * x).t / x.rows.toDouble
    grad(slice) += this.lambda * w(slice)
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
