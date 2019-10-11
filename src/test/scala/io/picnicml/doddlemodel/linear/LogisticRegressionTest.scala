package io.picnicml.doddlemodel.linear

import breeze.linalg.{DenseMatrix, DenseVector, convert}
import breeze.numerics.round
import io.picnicml.doddlemodel.TestingUtils
import io.picnicml.doddlemodel.data.{Features, RealVector, Target}
import io.picnicml.doddlemodel.linear.LogisticRegression.ev
import org.scalactic.{Equality, TolerantNumerics}
import org.scalatest.{FlatSpec, Matchers}

class LogisticRegressionTest extends FlatSpec with Matchers with TestingUtils {

  implicit val tolerance: Equality[Float] = TolerantNumerics.tolerantFloatEquality(1e-3f)

  "Logistic regression" should "calculate the value of the loss function" in {
    val w = DenseVector(1.0f, 2.0f, 3.0f)
    val x = DenseMatrix(
      List(3.0f, 1.0f, 2.0f),
      List(-1.0f, -2.0f, 2.0f)
    )
    val y = DenseVector(1.0f, 0.0f)

    val model = LogisticRegression(lambda = 1)
    ev.lossStateless(model, w, x, y) shouldEqual 7.1566391945397703f
  }

  it should "calculate the gradient of the loss function wrt. to model parameters" in {
    for (_ <- 1 to 1000) {
      val w = DenseVector.rand[Float](5, rand = randomUniform)
      val x = DenseMatrix.rand[Float](10, 5, rand = randomUniform)
      val y = convert(round(DenseVector.rand[Float](10, rand = randomUniform)), Float)
      testGrad(w, x, y)
    }

    def testGrad(w: RealVector, x: Features, y: Target) = {
      val model = LogisticRegression(lambda = 0.5f)
      breezeEqual(
        gradApprox(w => ev.lossStateless(model, w, x, y), w),
        ev.lossGradStateless(model, w, x, y)
      ) shouldEqual true
    }
  }


  it should "prevent the usage of negative L2 regularization strength" in {
    an [IllegalArgumentException] shouldBe thrownBy(LogisticRegression(lambda = -0.5f))
  }

  it should "throw an exception if fitting a model on a dataset with more than two classes" in {
    val x = DenseMatrix(
      List(3.0f, 1.0f, 2.0f),
      List(-1.0f, -2.0f, 2.0f),
      List(3.0f, 1.0f, 2.0f)
    )
    val y = DenseVector(1.0f, 0.0f, 2.0f)
    val model = LogisticRegression()

    an [IllegalArgumentException] shouldBe thrownBy(ev.fit(model, x, y))
  }
}
