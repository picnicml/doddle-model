package io.picnicml.doddlemodel.linear

import breeze.linalg._
import breeze.numerics.{exp, log, pow}
import cats.syntax.option._
import io.picnicml.doddlemodel.data.{Features, RealVector, Simplex, Target}
import io.picnicml.doddlemodel.linear.typeclasses.LinearClassifier
import io.picnicml.doddlemodel.syntax.OptionSyntax._

/** An immutable multiple multinomial regression model with ridge regularization.
  *
  * @param lambda L2 regularization strength, must be positive, 0 means no regularization
  *
  * Examples:
  * val model = SoftmaxClassifier()
  * val model = SoftmaxClassifier(lambda = 1.5f)
  */
case class SoftmaxClassifier private (lambda: Float, numClasses: Option[Int], private val w: Option[RealVector]) {
  private var yPredProbaCache: Simplex = _
}

object SoftmaxClassifier {

  def apply(lambda: Float = 0.0f): SoftmaxClassifier = {
    require(lambda >= 0.0f, "L2 regularization strength must be non-negative")
    SoftmaxClassifier(lambda, none, none)
  }

  private val wSlice: Range.Inclusive = 1 to -1

  @SerialVersionUID(0L)
  implicit lazy val ev: LinearClassifier[SoftmaxClassifier] = new LinearClassifier[SoftmaxClassifier] {

    override def numClasses(model: SoftmaxClassifier): Option[Int] = model.numClasses

    override protected def w(model: SoftmaxClassifier): Option[RealVector] = model.w

    override protected[doddlemodel] def copy(model: SoftmaxClassifier, numClasses: Int): SoftmaxClassifier =
      model.copy(numClasses = numClasses.some)

    override protected def copy(model: SoftmaxClassifier, w: RealVector): SoftmaxClassifier =
      model.copy(w = w.some)

    override protected def predictStateless(model: SoftmaxClassifier, w: RealVector, x: Features): Target =
      convert(argmax(predictProbaStateless(model, w, x)(*, ::)), Float)

    override protected def predictProbaStateless(model: SoftmaxClassifier, w: RealVector, x: Features): Simplex = {
      val z = x * w.asDenseMatrix.reshape(x.cols, model.numClasses.getOrBreak - 1, View.Require)
      val maxZ = max(z)
      val zExpPivot = DenseMatrix.horzcat(exp(z - maxZ), DenseMatrix.fill[Float](x.rows, 1)(exp(-maxZ)))
      zExpPivot(::, *) /:/ sum(zExpPivot(*, ::))
    }

    override protected[linear] def lossStateless(model: SoftmaxClassifier,
                                                 w: RealVector, x: Features, y: Target): Float = {
      model.yPredProbaCache = predictProbaStateless(model, w, x)
      val yPredProbaOfTrueClass = 0 until x.rows map { rowIndex =>
        val targetClass = y(rowIndex).toInt
        model.yPredProbaCache(rowIndex, targetClass)
      }

      val wMatrix = w.asDenseMatrix.reshape(x.cols, model.numClasses.getOrBreak - 1, View.Require)
      sum(log(DenseMatrix(yPredProbaOfTrueClass))) / (-x.rows.toFloat) +
        .5f * model.lambda * sum(pow(wMatrix(wSlice, ::), 2))
    }

    override protected[linear] def lossGradStateless(model: SoftmaxClassifier,
                                                     w: RealVector, x: Features, y: Target): RealVector = {
      val yPredProba = model.yPredProbaCache(::, 0 to -2)

      val indicator = DenseMatrix.zeros[Float](yPredProba.rows, yPredProba.cols)
      0 until indicator.rows foreach { rowIndex =>
        val targetClass = y(rowIndex).toInt
        if (targetClass < model.numClasses.getOrBreak - 1) indicator(rowIndex, targetClass) = 1.0f
      }

      val grad = (x.t * (indicator - yPredProba)) / (-x.rows.toFloat)
      val wMatrix = w.asDenseMatrix.reshape(x.cols, model.numClasses.getOrBreak - 1, View.Require)
      grad(wSlice, ::) += model.lambda * wMatrix(wSlice, ::)
      grad.toDenseVector
    }
  }
}
