package com.doddle.linear

import breeze.linalg.{DenseMatrix, DenseVector}
import breeze.optimize._
import com.doddle.TypeAliases.{RealMatrix, RealVector}
import com.doddle.base.Predictor

trait GeneralizedLinearModel[A] {
  this: Predictor[A] =>

  /** Parameters (weights) of a linear model. */
  protected val w: Option[RealVector]
  /** Should implement a function that returns a new instance with model parameters w. */
  protected def newInstance(w: RealVector): Predictor[A]
  /** Should implement a function that maps a real valued input to the support of a mean. */
  protected[linear] def meanFunction(latent: RealVector): DenseVector[A]
  /** Should implement a function that calculates the value of the loss function. */
  protected[linear] def loss(w: RealVector, x: RealMatrix, y: DenseVector[A]): Double
  /** Should implement a function that calculates the gradient of the loss function wrt. model parameters. */
  protected[linear] def lossGrad(w: RealVector, x: RealMatrix, y: DenseVector[A]): RealVector

  override def fit(x: RealMatrix, y: DenseVector[A]): Predictor[A] = {
    require(this.w.isEmpty, "Called fit on a model that is already trained")

    val xWithColOfOnes = this.xWithBiasTerm(x)
    val diffFunction = new DiffFunction[RealVector] {
      override def calculate(w: RealVector): (Double, RealVector) =
        (loss(w, xWithColOfOnes, y), lossGrad(w, xWithColOfOnes, y))
    }

    val lbfgs = new LBFGS[DenseVector[Double]]()
    val wSolution = lbfgs.minimize(diffFunction, DenseVector.zeros[Double](xWithColOfOnes.cols))
    this.newInstance(wSolution)
  }

  override def predict(x: RealMatrix): DenseVector[A] = {
    require(this.w.isDefined, "Called predict on a model that is not trained yet")
    predict(this.w.get, this.xWithBiasTerm(x))
  }

  protected def predict(w: RealVector, x: RealMatrix): DenseVector[A] =
    meanFunction(x * w)

  private def xWithBiasTerm(x: RealMatrix): RealMatrix =
    DenseMatrix.horzcat(DenseMatrix.ones[Double](x.rows, 1), x)
}
