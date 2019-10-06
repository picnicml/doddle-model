package io.picnicml.doddlemodel

import breeze.linalg.{DenseMatrix, DenseVector, convert, zipValues}
import breeze.optimize.ApproximateGradientFunction
import breeze.stats.distributions.Rand
import io.picnicml.doddlemodel.data.{Dataset, RealVector}
import org.scalactic.Equality

trait TestingUtils {

  implicit lazy val randomUniform: Rand[Float] = () => Rand.uniform.draw().toFloat

  def breezeEqual(x0: DenseMatrix[Float], x1: DenseMatrix[Float])(implicit tol: Equality[Float]): Boolean =
    breezeEqual(x0.toDenseVector, x1.toDenseVector)

  def breezeEqual(x0: RealVector, x1: RealVector)(implicit tol: Equality[Float]): Boolean =
    zipValues(x0, x1).forall((v0, v1) => (v0.isNaN && v1.isNaN) || tol.areEquivalent(v0, v1))

  def gradApprox(func: DenseVector[Double] => Double, x: RealVector): RealVector = {
    val gradApprox = new ApproximateGradientFunction(func)
    convert(gradApprox.gradientAt(convert(x, Double)), Float)
  }

  def dummyData(nRows: Int, nCols: Int = 1): Dataset =
    (DenseMatrix.zeros[Float](nRows, nCols), convert(DenseVector((0 until nRows).toArray), Float))
}
