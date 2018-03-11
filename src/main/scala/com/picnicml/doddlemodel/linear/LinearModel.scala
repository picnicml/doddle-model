package com.picnicml.doddlemodel.linear

import breeze.linalg.{DenseMatrix, DenseVector}
import breeze.optimize._
import com.picnicml.doddlemodel.base.Predictor
import com.picnicml.doddlemodel.data.Types.{Features, RealVector, Target}

trait LinearModel {
  this: Predictor =>

  /** Parameters (weights) of a linear model, i.e. the state of the model. */
  protected val w: Option[RealVector]

  /** A stateless function that predicts a target variable. */
  protected def predict(w: RealVector, x: Features): Target

  /** A stateless function that calculates the value of the loss function. */
  protected[linear] def loss(w: RealVector, x: Features, y: Target): Double

  /** A stateless function that calculates the gradient of the loss function wrt. model parameters. */
  protected[linear] def lossGrad(w: RealVector, x: Features, y: Target): RealVector

  override def predict(x: Features): Target = {
    require(this.isTrained, "Called predict on a model that is not trained yet")
    this.predict(this.w.get, this.xWithBiasTerm(x))
  }

  protected def findModelParameters(x: Features, y: Target): RealVector = {
    val xWithColOfOnes = this.xWithBiasTerm(x)
    val diffFunction = new DiffFunction[RealVector] {
      override def calculate(w: RealVector): (Double, RealVector) =
        (loss(w, xWithColOfOnes, y), lossGrad(w, xWithColOfOnes, y))
    }
    val lbfgs = new LBFGS[DenseVector[Double]](tolerance = 1e-4)
    lbfgs.minimize(diffFunction, DenseVector.zeros[Double](xWithColOfOnes.cols))
  }

  protected def isTrained: Boolean = this.w.isDefined

  protected def xWithBiasTerm(x: Features): Features =
    DenseMatrix.horzcat(DenseMatrix.ones[Double](x.rows, 1), x)
}
