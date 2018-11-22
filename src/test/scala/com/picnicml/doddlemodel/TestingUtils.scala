package com.picnicml.doddlemodel

import breeze.linalg.{DenseMatrix, DenseVector, convert, zipValues}
import breeze.optimize.ApproximateGradientFunction
import com.picnicml.doddlemodel.data.{Dataset, RealVector}
import org.scalactic.Equality

trait TestingUtils {

  def breezeEqual(x0: RealVector, x1: RealVector)(implicit tol: Equality[Double]): Boolean =
    zipValues(x0, x1).forall((v0, v1) => tol.areEquivalent(v0, v1))

  def gradApprox(func: RealVector => Double, x: RealVector): RealVector = {
    val gradApprox = new ApproximateGradientFunction(func)
    gradApprox.gradientAt(x)
  }

  def dummyData(nRows: Int): Dataset =
    (DenseMatrix.zeros[Double](nRows, 1), convert(DenseVector((0 until nRows).toArray), Double))
}
