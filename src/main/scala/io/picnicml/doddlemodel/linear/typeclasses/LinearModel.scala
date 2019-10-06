package io.picnicml.doddlemodel.linear.typeclasses

import breeze.linalg.{DenseMatrix, DenseVector}
import breeze.optimize.{DiffFunction, LBFGS}
import io.picnicml.doddlemodel.data.{Features, RealVector, Target}
import io.picnicml.doddlemodel.typeclasses.Predictor

trait LinearModel[A] {
  this: Predictor[A] =>

  /** Parameters (weights) of a linear model, i.e. the state of the model. */
  protected def w(model: A): Option[RealVector]

  /** A function that creates a new linear model with parameters w. */
  protected def copy(model: A, w: RealVector): A

  /** A stateless function that predicts a target variable. */
  protected def predictStateless(model: A, w: RealVector, x: Features): Target

  /** A stateless function that calculates the value of the loss function. */
  protected[linear] def lossStateless(model: A, w: RealVector, x: Features, y: Target): Float

  /** A stateless function that calculates the gradient of the loss function wrt. model parameters. */
  protected[linear] def lossGradStateless(model: A, w: RealVector, x: Features, y: Target): RealVector

  override def isFitted(model: A): Boolean = w(model).isDefined

  override def predictSafe(model: A, x: Features): Target =
    predictStateless(model, w(model).get, xWithBiasTerm(x))

  protected def maximumLikelihood(model: A, x: Features, y: Target, init: RealVector): RealVector = {
    val diffFunction = new DiffFunction[RealVector] {
      override def calculate(w: RealVector): (Double, RealVector) =
        (lossStateless(model, w, x, y).toDouble, lossGradStateless(model, w, x, y))
    }
    val lbfgs = new LBFGS[DenseVector[Float]](tolerance = 1e-4)
    lbfgs.minimize(diffFunction, init)
  }

  protected def xWithBiasTerm(x: Features): Features =
    DenseMatrix.horzcat(DenseMatrix.ones[Float](x.rows, 1), x)
}
