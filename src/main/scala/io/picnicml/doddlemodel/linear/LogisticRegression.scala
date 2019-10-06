package io.picnicml.doddlemodel.linear

import breeze.linalg.sum
import breeze.numerics.{log, sigmoid}
import cats.syntax.option._
import io.picnicml.doddlemodel.data.{Features, RealVector, Simplex, Target}
import io.picnicml.doddlemodel.linear.typeclasses.LinearClassifier

/** An immutable multiple logistic regression model with ridge regularization.
  *
  * @param lambda L2 regularization strength, must be positive, 0 means no regularization
  *
  * Examples:
  * val model = LogisticRegression()
  * val model = LogisticRegression(lambda = 1.5f)
  */
case class LogisticRegression private (lambda: Float, numClasses: Option[Int], private val w: Option[RealVector]) {
  private var yPredProbaCache: RealVector = _
}

object LogisticRegression {

  def apply(lambda: Float = 0.0f): LogisticRegression = {
    require(lambda >= 0.0f, "L2 regularization strength must be non-negative")
    LogisticRegression(lambda, none, none)
  }

  private val wSlice: Range.Inclusive = 1 to -1

  @SerialVersionUID(0L)
  implicit lazy val ev: LinearClassifier[LogisticRegression] = new LinearClassifier[LogisticRegression] {

    override def numClasses(model: LogisticRegression): Option[Int] = model.numClasses

    override protected def w(model: LogisticRegression): Option[RealVector] = model.w

    override protected[doddlemodel] def copy(model: LogisticRegression, numClasses: Int): LogisticRegression =
      model.copy(numClasses = numClasses.some)

    override protected def copy(model: LogisticRegression, w: RealVector): LogisticRegression =
      model.copy(w = w.some)

    override protected def predictStateless(model: LogisticRegression, w: RealVector, x: Features): Target =
      (predictProbaStateless(model, w, x)(::, 0) >:> 0.5f).map(x => if (x) 1.0f else 0.0f)

    override protected def predictProbaStateless(model: LogisticRegression, w: RealVector, x: Features): Simplex =
      sigmoid(x * w).asDenseMatrix.t

    override protected[linear] def lossStateless(model: LogisticRegression,
                                                 w: RealVector, x: Features, y: Target): Float = {
      model.yPredProbaCache = predictProbaStateless(model, w, x)(::, 0)
      sum(y * log(model.yPredProbaCache) + (1.0f - y) * log(1.0f - model.yPredProbaCache)) / (-x.rows.toFloat) +
        .5f * model.lambda * (w(wSlice).t * w(wSlice))
    }

    override protected[linear] def lossGradStateless(model: LogisticRegression,
                                                     w: RealVector, x: Features, y: Target): RealVector = {
      val grad = ((y - model.yPredProbaCache).t * x).t / (-x.rows.toFloat)
      grad(wSlice) += model.lambda * w(wSlice)
      grad
    }
  }
}
