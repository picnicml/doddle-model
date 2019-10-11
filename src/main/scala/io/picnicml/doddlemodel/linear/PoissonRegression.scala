package io.picnicml.doddlemodel.linear

import breeze.linalg.{all, sum}
import breeze.numerics.{exp, floor, isFinite, log}
import cats.syntax.option._
import io.picnicml.doddlemodel.data.{Features, RealVector, Target}
import io.picnicml.doddlemodel.linear.typeclasses.LinearRegressor

/** An immutable multiple Poisson regression model with ridge regularization.
  *
  * @param lambda L2 regularization strength, must be positive, 0 means no regularization
  *
  * Examples:
  * val model = PoissonRegression()
  * val model = PoissonRegression(lambda = 1.5f)
  */
case class PoissonRegression private (lambda: Float, private val w: Option[RealVector]) {
  private var yPredMeanCache: Target = _
}

object PoissonRegression {

  def apply(lambda: Float = 0.0f): PoissonRegression = {
    require(lambda >= 0.0f, "L2 regularization strength must be non-negative")
    PoissonRegression(lambda, none)
  }

  private val wSlice: Range.Inclusive = 1 to -1

  @SerialVersionUID(0L)
  implicit lazy val ev: LinearRegressor[PoissonRegression] = new LinearRegressor[PoissonRegression] {

    override protected def w(model: PoissonRegression): Option[RealVector] = model.w

    override protected def copy(model: PoissonRegression): PoissonRegression = model.copy()

    override protected def copy(model: PoissonRegression, w: RealVector): PoissonRegression =
      model.copy(w = w.some)

    override protected def targetVariableAppropriate(y: Target): Boolean =
      y == floor(y) && all(isFinite(y))

    override protected def predictStateless(model: PoissonRegression, w: RealVector, x: Features): Target =
      floor(this.predictMean(w, x))

    private def predictMean(w: RealVector, x: Features): Target = exp(x * w)

    override protected[linear] def lossStateless(model: PoissonRegression,
                                                 w: RealVector, x: Features, y: Target): Float = {
      model.yPredMeanCache = predictMean(w, x)
      sum(y * log(model.yPredMeanCache) - model.yPredMeanCache) / (-x.rows.toFloat) +
        .5f * model.lambda * (w(wSlice).t * w(wSlice))
    }

    override protected[linear] def lossGradStateless(model: PoissonRegression,
                                                     w: RealVector, x: Features, y: Target): RealVector = {
      val grad = ((model.yPredMeanCache - y).t * x).t / x.rows.toFloat
      grad(wSlice) += model.lambda * w(wSlice)
      grad
    }
  }
}
