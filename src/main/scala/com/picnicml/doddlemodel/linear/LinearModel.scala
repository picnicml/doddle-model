package com.picnicml.doddlemodel.linear

import breeze.linalg.{DenseMatrix, DenseVector}
import breeze.optimize._
import com.picnicml.doddlemodel.data.{Features, RealVector, Target}
import com.picnicml.doddlemodel.typeclasses.Predictor

trait LinearModel[A] {
  this: Predictor[A] =>

  /** Parameters (weights) of a linear model, i.e. the state of the model. */
  protected def w(model: A): Option[RealVector]

  /** A function that creates a new linear model with parameters w. */
  protected def copy(model: A, w: RealVector): A

  /** A stateless function that predicts a target variable. */
  protected def predict(model: A, w: RealVector, x: Features): Target

  /** A stateless function that calculates the value of the loss function. */
  protected[linear] def loss(model: A, w: RealVector, x: Features, y: Target): Double

  /** A stateless function that calculates the gradient of the loss function wrt. model parameters. */
  protected[linear] def lossGrad(model: A, w: RealVector, x: Features, y: Target): RealVector

  final override def isFitted(model: A): Boolean = this.w(model).isDefined

  final override def predictSafe(model: A, x: Features): Target =
    this.predict(model, this.w(model: A).get, this.xWithBiasTerm(x))

  final protected def maximumLikelihood(model: A, x: Features, y: Target, init: RealVector): RealVector = {
    val diffFunction = new DiffFunction[RealVector] {
      override def calculate(w: RealVector): (Double, RealVector) =
        (loss(model, w, x, y), lossGrad(model, w, x, y))
    }
    val lbfgs = new LBFGS[DenseVector[Double]](tolerance = 1e-4)
    lbfgs.minimize(diffFunction, init)
  }

  final protected def xWithBiasTerm(x: Features): Features =
    DenseMatrix.horzcat(DenseMatrix.ones[Double](x.rows, 1), x)
}
