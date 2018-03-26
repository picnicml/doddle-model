package com.picnicml.doddlemodel.linear

import breeze.linalg.{*, DenseMatrix, View, argmax, convert, sum}
import breeze.numerics.{exp, log, pow}
import com.picnicml.doddlemodel.base.Classifier
import com.picnicml.doddlemodel.data.{Features, RealVector, Simplex, Target}

/** An immutable multiple multinomial regression model with ridge regularization.
  *
  * @param lambda L2 regularization strength, must be positive, 0 means no regularization
  *
  * Examples:
  * val model = SoftmaxClassifier()
  * val model = SoftmaxClassifier(lambda = 1.5)
  */
class SoftmaxClassifier private (val lambda: Double, val numClasses: Option[Int], protected val w: Option[RealVector])
  extends LinearClassifier {

  override protected[linear] def copy(numClasses: Int): LinearClassifier = {
    // todo: suggest logistic regression if numClasses == 2
    new SoftmaxClassifier(this.lambda, Some(numClasses), this.w)
  }

  override protected def copy(w: RealVector): Classifier =
    new SoftmaxClassifier(this.lambda, this.numClasses, Some(w))

  override protected def predict(w: RealVector, x: Features): Target =
    convert(argmax(this.predictProba(w, x)(*, ::)), Double)

  override protected def predictProba(w: RealVector, x: Features): Simplex = {
    require(this.numClasses.isDefined)

    val zExp = exp(x * w.asDenseMatrix.reshape(x.cols, this.numClasses.get - 1, View.Require))
    val zExpPivot = DenseMatrix.horzcat(zExp, DenseMatrix.ones[Double](x.rows, 1))
    zExpPivot(::, *) /:/ sum(zExpPivot(*, ::))
  }

  override protected[linear] def loss(w: RealVector, x: Features, y: Target): Double = {
    require(this.numClasses.isDefined)
    val yPredProba = this.predictProba(w, x)

    // todo: vectorize
    val yPredProbaOfTrueClass = 0 until x.rows map { rowIndex =>
      val targetClass = y(rowIndex).toInt
      yPredProba(rowIndex, targetClass)
    }

    val wMatrix = w.asDenseMatrix.reshape(x.cols, this.numClasses.get - 1, View.Require)
    sum(log(DenseMatrix(yPredProbaOfTrueClass))) / (-x.rows.toDouble) +
      .5 * this.lambda * sum(pow(wMatrix(1 to -1, ::), 2))
  }

  override protected[linear] def lossGrad(w: RealVector, x: Features, y: Target): RealVector = {
    require(this.numClasses.isDefined)
    val yPredProba = this.predictProba(w, x)(::, 0 to -2)

    // todo: vectorize
    val indicator = DenseMatrix.zeros[Double](yPredProba.rows, yPredProba.cols)
    0 until indicator.rows foreach { rowIndex =>
      val targetClass = y(rowIndex).toInt
      if (targetClass < this.numClasses.get - 1) indicator(rowIndex, targetClass) = 1.0
    }

    val grad = (x.t * (indicator - yPredProba)) / (-x.rows.toDouble)
    val wMatrix = w.asDenseMatrix.reshape(x.cols, this.numClasses.get - 1, View.Require)
    grad(1 to -1, ::) += this.lambda * wMatrix(1 to -1, ::)
    grad.toDenseVector
  }
}

object SoftmaxClassifier {

  def apply(): SoftmaxClassifier = new SoftmaxClassifier(0, None, None)

  def apply(lambda: Double): SoftmaxClassifier = {
    require(lambda > 0, "L2 regularization strength must be positive")
    new SoftmaxClassifier(lambda, None, None)
  }
}
