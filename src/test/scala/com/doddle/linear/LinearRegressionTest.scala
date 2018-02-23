package com.doddle.linear

import breeze.linalg.{DenseMatrix, DenseVector}
import breeze.optimize.ApproximateGradientFunction
import com.doddle.TestUtils
import com.doddle.TypeAliases.{RealMatrix, RealVector}
import org.scalactic.{Equality, TolerantNumerics}
import org.scalatest.{FlatSpec, Matchers}

class LinearRegressionTest extends FlatSpec with Matchers with TestUtils {

  implicit val doubleTolerance: Equality[Double] = TolerantNumerics.tolerantDoubleEquality(1e-4)

  "Linear regression" should "calculate the value of the loss function" in {
    val w = DenseVector(1.0, 2.0, 3.0)
    val x = DenseMatrix((3.0, 1.0, 2.0), (-1.0, -2.0, 2.0))
    val y = DenseVector(3.0, 4.0)

    val model = LinearRegression(lambda = 1)
    model.loss(w, x, y) shouldEqual 24.75
  }

  it should "calculate the gradient of the loss function wrt. to model parameters" in {
    for (_ <- 1 to 1000) {
      val w = DenseVector.rand[Double](5)
      val x = DenseMatrix.rand[Double](10, 5)
      val y = DenseVector.rand[Double](10)
      testGrad(w, x, y)
    }

    def testGrad(w: RealVector, x: RealMatrix, y: RealVector) = {
      val model = LinearRegression(lambda = 0.5)
      val gradApprox = new ApproximateGradientFunction((w: RealVector) => model.loss(w, x, y))
      breezeEqual(gradApprox.gradientAt(w), model.lossGrad(w, x, y)) shouldEqual true
    }
  }
}
