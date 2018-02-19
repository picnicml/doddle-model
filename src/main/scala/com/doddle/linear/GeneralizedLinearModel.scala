package com.doddle.linear

import breeze.linalg.{DenseMatrix, DenseVector}
import breeze.optimize._
import com.doddle.Types.{RealMatrix, RealVector}
import com.doddle.base.Predictor

trait GeneralizedLinearModel[A] {
  this: Predictor[A] =>

  private[linear] def meanFunction(latent: RealVector): DenseVector[A]
  private[linear] def loss(w: RealVector, x: RealMatrix, y: DenseVector[A]): Double
  private[linear] def lossGrad(w: RealVector, x: RealMatrix, y: DenseVector[A]): RealVector

  private var w: RealVector = _

  override def fit(x: RealMatrix, y: DenseVector[A]): Unit = {
    require(this.w == null, "Called fit on a model that is already trained")

    val xWithOnes = xWithBiasTerm(x)
    val diffFunction = new DiffFunction[RealVector] {
      override def calculate(a: RealVector): (Double, RealVector) =
        (loss(a, xWithOnes, y), lossGrad(a, xWithOnes, y))
    }

    this.w = minimize(diffFunction, DenseVector.zeros[Double](xWithOnes.cols))
  }

  override def predict(x: RealMatrix): DenseVector[A] = {
    require(this.w != null, "Called predict on a model that is not trained yet")
    predict(this.w, xWithBiasTerm(x))
  }

  protected def predict(w: RealVector, x: RealMatrix): DenseVector[A] = meanFunction((w.t * x.t).t)

  private def xWithBiasTerm(x: RealMatrix): RealMatrix = DenseMatrix.horzcat(DenseMatrix.ones[Double](x.rows, 1), x)
}
