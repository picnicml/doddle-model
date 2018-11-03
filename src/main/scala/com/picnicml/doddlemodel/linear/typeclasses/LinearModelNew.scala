package com.picnicml.doddlemodel.linear.typeclasses

import breeze.linalg.{DenseMatrix, DenseVector}
import breeze.optimize.{DiffFunction, LBFGS}
import com.picnicml.doddlemodel.typeclasses.Predictor
import com.picnicml.doddlemodel.data.{Features, RealVector, Target}

trait LinearModelNew[A] {
  this: Predictor[A] =>

  /** Parameters (weights) of a linear model, i.e. the state of the model. */
  protected def w(model: A): Option[RealVector]

  /** A function that creates a new linear model with parameters w. */
  protected def copy(model: A, w: RealVector): A

  /** A stateless function that predicts a target variable. */
  protected def predict(w: RealVector, x: Features): Target

  /** A stateless function that calculates the value of the loss function. */
  protected[linear] def loss(model: A, w: RealVector, x: Features, y: Target): Double

  /** A stateless function that calculates the gradient of the loss function wrt. model parameters. */
  protected[linear] def lossGrad(model: A, w: RealVector, x: Features, y: Target): RealVector

  override def isFitted(model: A): Boolean = w(model).isDefined

  override def predictSafe(model: A, x: Features): Target =
    predict(w(model).get, xWithBiasTerm(x))

  protected def maximumLikelihood(model: A, x: Features, y: Target, init: RealVector): RealVector = {
    val diffFunction = new DiffFunction[RealVector] {
      override def calculate(w: RealVector): (Double, RealVector) =
        (loss(model, w, x, y), lossGrad(model, w, x, y))
    }
    val lbfgs = new LBFGS[DenseVector[Double]](tolerance = 1e-4)
    lbfgs.minimize(diffFunction, init)
  }

  protected def xWithBiasTerm(x: Features): Features =
    DenseMatrix.horzcat(DenseMatrix.ones[Double](x.rows, 1), x)
}
