package com.picnicml.doddlemodel

import breeze.linalg.{DenseMatrix, DenseVector, convert, zipValues}
import breeze.optimize.ApproximateGradientFunction
import com.picnicml.doddlemodel.data.{Dataset, RealVector}
import org.scalactic.Equality

trait TestingUtils {

  def breezeEqual(x0: DenseMatrix[Double], x1: DenseMatrix[Double])(implicit tol: Equality[Double]): Boolean =
    breezeEqual(x0.toDenseVector, x1.toDenseVector)

  def breezeEqual(x0: RealVector, x1: RealVector)(implicit tol: Equality[Double]): Boolean =
    zipValues(x0, x1).forall((v0, v1) => tol.areEquivalent(v0, v1))

  def gradApprox(func: RealVector => Double, x: RealVector): RealVector = {
    val gradApprox = new ApproximateGradientFunction(func)
    gradApprox.gradientAt(x)
  }

  def dummyData(nRows: Int, nCols: Int = 1): Dataset =
    (DenseMatrix.zeros[Double](nRows, nCols), convert(DenseVector((0 until nRows).toArray), Double))
}
