package io.picnicml.doddlemodel.linear

import breeze.linalg.{DenseMatrix, DenseVector}
import io.picnicml.doddlemodel.TestingUtils
import io.picnicml.doddlemodel.data.{Features, RealVector, Target}
import io.picnicml.doddlemodel.linear.LinearRegression.ev
import org.scalactic.{Equality, TolerantNumerics}
import org.scalatest.{FlatSpec, Matchers}

class LinearRegressionTest extends FlatSpec with Matchers with TestingUtils {

  implicit val tolerance: Equality[Float] = TolerantNumerics.tolerantFloatEquality(1e-3f)

  "Linear regression" should "calculate the value of the loss function" in {
    val w = DenseVector(1.0f, 2.0f, 3.0f)
    val x = DenseMatrix(
      List(3.0f, 1.0f, 2.0f),
      List(-1.0f, -2.0f, 2.0f)
    )
    val y = DenseVector(3.0f, 4.0f)

    val model = LinearRegression(lambda = 1)
    ev.lossStateless(model, w, x, y) shouldEqual 24.75f
  }

  it should "calculate the gradient of the loss function wrt. to model parameters" in {
    for (_ <- 1 to 1000) {
      val w = DenseVector.rand[Float](5, rand = randomUniform)
      val x = DenseMatrix.rand[Float](10, 5, rand = randomUniform)
      val y = DenseVector.rand[Float](10, rand = randomUniform)
      testGrad(w, x, y)
    }

    def testGrad(w: RealVector, x: Features, y: Target) = {
      val model = LinearRegression(lambda = 0.5f)
      breezeEqual(
        gradApprox(w => ev.lossStateless(model, w, x, y), w),
        ev.lossGradStateless(model, w, x, y)
      ) shouldEqual true
    }
  }

  it should "prevent the usage of negative L2 regularization strength" in {
    an [IllegalArgumentException] shouldBe thrownBy(LinearRegression(lambda = -0.5f))
  }
}
