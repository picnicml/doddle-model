package com.picnicml.doddlemodel

import breeze.linalg.zipValues
import breeze.optimize.ApproximateGradientFunction
import com.picnicml.doddlemodel.data.RealVector
import org.scalactic.Equality

trait TestUtils {

  def breezeEqual(x0: RealVector, x1: RealVector)(implicit tol: Equality[Double]): Boolean =
    zipValues(x0, x1).forall((v0, v1) => tol.areEquivalent(v0, v1))

  def gradApprox(func: RealVector => Double, x: RealVector): RealVector = {
    val gradApprox = new ApproximateGradientFunction(func)
    gradApprox.gradientAt(x)
  }
}
