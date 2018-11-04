package com.picnicml.doddlemodel.linear

import breeze.linalg._
import breeze.numerics.{exp, log, pow}
import com.picnicml.doddlemodel.data.{Features, RealVector, Simplex, Target}
import com.picnicml.doddlemodel.linear.typeclasses.LinearClassifier

/** An immutable multiple multinomial regression model with ridge regularization.
  *
  * @param lambda L2 regularization strength, must be positive, 0 means no regularization
  *
  * Examples:
  * val model = SoftmaxClassifier()
  * val model = SoftmaxClassifier(lambda = 1.5)
  */
case class SoftmaxClassifier private(lambda: Double, numClasses: Option[Int], private val w: Option[RealVector]) {
  private var yPredProbaCache: Simplex = _
}

object SoftmaxClassifier {

  def apply(): SoftmaxClassifier = SoftmaxClassifier(0, None, None)

  def apply(lambda: Double): SoftmaxClassifier = {
    require(lambda > 0, "L2 regularization strength must be positive")
    SoftmaxClassifier(lambda, None, None)
  }

  private val wSlice: Range.Inclusive = 1 to -1

  implicit lazy val ev: LinearClassifier[SoftmaxClassifier] = new LinearClassifier[SoftmaxClassifier] {

    override def numClasses(model: SoftmaxClassifier): Option[Int] = model.numClasses

    override protected def w(model: SoftmaxClassifier): Option[RealVector] = model.w

    override protected[doddlemodel] def copy(model: SoftmaxClassifier, numClasses: Int): SoftmaxClassifier =
      model.copy(numClasses = Some(numClasses))

    override protected def copy(model: SoftmaxClassifier, w: RealVector): SoftmaxClassifier =
      model.copy(w = Some(w))

    override protected def predictStateless(model: SoftmaxClassifier, w: RealVector, x: Features): Target =
      convert(argmax(predictProbaStateless(model, w, x)(*, ::)), Double)

    override protected def predictProbaStateless(model: SoftmaxClassifier, w: RealVector, x: Features): Simplex = {
      val numClasses = model.numClasses match {
        case Some(nc) => nc
        case None => throw new IllegalStateException("numClasses not set on a trained model")
      }

      val z = x * w.asDenseMatrix.reshape(x.cols, numClasses - 1, View.Require)
      val maxZ = max(z)
      val zExpPivot = DenseMatrix.horzcat(exp(z - maxZ), DenseMatrix.fill[Double](x.rows, 1)(exp(-maxZ)))
      zExpPivot(::, *) /:/ sum(zExpPivot(*, ::))
    }

    override protected[linear] def lossStateless(model: SoftmaxClassifier,
                                                 w: RealVector, x: Features, y: Target): Double = {
      val numClasses = model.numClasses match {
        case Some(nc) => nc
        case None => throw new IllegalStateException("numClasses must be set during training")
      }

      model.yPredProbaCache = predictProbaStateless(model, w, x)
      val yPredProbaOfTrueClass = 0 until x.rows map { rowIndex =>
        val targetClass = y(rowIndex).toInt
        model.yPredProbaCache(rowIndex, targetClass)
      }

      val wMatrix = w.asDenseMatrix.reshape(x.cols, numClasses - 1, View.Require)
      sum(log(DenseMatrix(yPredProbaOfTrueClass))) / (-x.rows.toDouble) +
        .5 * model.lambda * sum(pow(wMatrix(wSlice, ::), 2))
    }

    override protected[linear] def lossGradStateless(model: SoftmaxClassifier,
                                                     w: RealVector, x: Features, y: Target): RealVector = {
      val numClasses = model.numClasses match {
        case Some(nc) => nc
        case None => throw new IllegalStateException("numClasses must be set during training")
      }

      val yPredProba = model.yPredProbaCache(::, 0 to -2)

      val indicator = DenseMatrix.zeros[Double](yPredProba.rows, yPredProba.cols)
      0 until indicator.rows foreach { rowIndex =>
        val targetClass = y(rowIndex).toInt
        if (targetClass < numClasses - 1) indicator(rowIndex, targetClass) = 1.0
      }

      val grad = (x.t * (indicator - yPredProba)) / (-x.rows.toDouble)
      val wMatrix = w.asDenseMatrix.reshape(x.cols, numClasses - 1, View.Require)
      grad(wSlice, ::) += model.lambda * wMatrix(wSlice, ::)
      grad.toDenseVector
    }
  }
}
