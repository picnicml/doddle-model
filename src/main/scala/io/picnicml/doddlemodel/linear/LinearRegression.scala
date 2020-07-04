package io.picnicml.doddlemodel.linear

import cats.syntax.option._
import io.picnicml.doddlemodel.data.{Features, RealVector, Target}
import io.picnicml.doddlemodel.linear.typeclasses.LinearRegressor

case class LinearRegression private (lambda: Float, private val w: Option[RealVector]) {
  private var yPredCache: Target = _
}

/** An immutable multiple linear regression model with ridge regularization. */
object LinearRegression {

  /** Create a regularized linear regression model.
    *
    * @param lambda L2 regularization strength - must be non-negative, 0.0 means no regularization
    *
    * @example Create and fit a regularized linear regression model with lambda 1.5.
    *   {{{
    *     import breeze.linalg.{DenseMatrix, DenseVector}
    *     import io.picnicml.doddlemodel.linear.LinearRegression
    *     import io.picnicml.doddlemodel.syntax.RegressorSyntax._
    *
    *     val X = DenseMatrix(List(1.0f, 2.0f), List(3.0f, 4.0f))
    *     val y = DenseVector(-3.0f, 2.0f)
    *     val model = LinearRegression(lambda = 1.5f)
    *     val fittedModel = model.fit(X, y)
    *   }}}
    */
  def apply(lambda: Float = 0.0f): LinearRegression = {
    require(lambda >= 0.0f, "L2 regularization strength must be non-negative")
    LinearRegression(lambda, none)
  }

  private val wSlice: Range.Inclusive = 1 to -1

  @SerialVersionUID(0L)
  implicit lazy val ev: LinearRegressor[LinearRegression] = new LinearRegressor[LinearRegression] {

    override protected def w(model: LinearRegression): Option[RealVector] = model.w

    override protected def copy(model: LinearRegression): LinearRegression =
      model.copy()

    override protected def copy(model: LinearRegression, w: RealVector): LinearRegression =
      model.copy(w = w.some)

    @inline override protected def targetVariableAppropriate(y: Target): Boolean = true

    override protected def predictStateless(model: LinearRegression, w: RealVector, x: Features): Target = x * w

    override protected[linear] def lossStateless(model: LinearRegression,
                                                 w: RealVector, x: Features, y: Target): Float = {
      model.yPredCache = predictStateless(model, w, x)
      val d = y - model.yPredCache
      .5f * (((d.t * d) / x.rows.toFloat) + model.lambda * (w(wSlice).t * w(wSlice)))
    }

    override protected[linear] def lossGradStateless(model: LinearRegression,
                                                     w: RealVector, x: Features, y: Target): RealVector = {
      val grad = ((y - model.yPredCache).t * x).t / (-x.rows.toFloat)
      grad(wSlice) += model.lambda * w(wSlice)
      grad
    }
  }
}
