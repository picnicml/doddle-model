package io.picnicml.doddlemodel.linear

import breeze.linalg.{all, sum}
import breeze.numerics.{exp, floor, isFinite, log}
import cats.syntax.option._
import io.picnicml.doddlemodel.data.{Features, RealVector, Target}
import io.picnicml.doddlemodel.linear.typeclasses.LinearRegressor

/** An immutable multiple Poisson regression model with ridge regularization. */
case class PoissonRegression private (lambda: Float, private val w: Option[RealVector]) {
  private var yPredMeanCache: Target = _
}

object PoissonRegression {

  /** Create a regularized Poisson regression model.
    *
    * @param lambda L2 regularization strength, must be non-negative, 0.0 means no regularization
    *
    * @example Create and fit a regularized Poisson regression model with lambda = 1.5.
    *   {{{
    *     import io.picnicml.doddlemodel.linear.PoissonRegression.ev
    *
    *     val X: Features = DenseMatrix(List(1.0, 2.0), List(3.0, 4.0))
    *     val y: Target = DenseVector(-3.0, 2.0)
    *     val model = PoissonRegression(lambda = 1.5f)
    *     val fittedModel = ev.fit(model, X, y)
    *   }}}
    */
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
