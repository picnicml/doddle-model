package com.doddle.linear

import breeze.linalg.{DenseMatrix, DenseVector}
import breeze.optimize._
import com.doddle.Types.{RealMatrix, RealVector}
import com.doddle.base.Predictor

trait GeneralizedLinearModel[A] {
  this: Predictor[A] =>

  /** Should implement a function that maps a real valued input to the support of a mean. */
  protected[linear] def meanFunction(latent: RealVector): DenseVector[A]

  /** Should implement a function that calculates the value of the loss function. */
  protected[linear] def loss(w: RealVector, x: RealMatrix, y: DenseVector[A]): Double

  /** Should implement a function that calculates the gradient of the loss function wrt. model parameters. */
  protected[linear] def lossGrad(w: RealVector, x: RealMatrix, y: DenseVector[A]): RealVector

  private var w: RealVector = _

  override def fit(x: RealMatrix, y: DenseVector[A]): Unit = {
    require(this.w == null, "Called fit on a model that is already trained")

    val xWithColOfOnes = xWithBiasTerm(x)
    val diffFunction = new DiffFunction[RealVector] {
      override def calculate(w: RealVector): (Double, RealVector) =
        (loss(w, xWithColOfOnes, y), lossGrad(w, xWithColOfOnes, y))
    }

    val lbfgs = new LBFGS[DenseVector[Double]]()
    this.w = lbfgs.minimize(diffFunction, DenseVector.zeros[Double](xWithColOfOnes.cols))
  }

  override def predict(x: RealMatrix): DenseVector[A] = {
    require(this.w != null, "Called predict on a model that is not trained yet")
    predict(this.w, xWithBiasTerm(x))
  }

  protected def predict(w: RealVector, x: RealMatrix): DenseVector[A] = meanFunction(x * w)

  private def xWithBiasTerm(x: RealMatrix): RealMatrix = DenseMatrix.horzcat(DenseMatrix.ones[Double](x.rows, 1), x)
}
