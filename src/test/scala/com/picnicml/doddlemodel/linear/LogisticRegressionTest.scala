package com.picnicml.doddlemodel.linear

import breeze.linalg.{DenseMatrix, DenseVector, convert}
import breeze.numerics.round
import com.picnicml.doddlemodel.TestUtils
import com.picnicml.doddlemodel.data.{Features, RealVector, Target}
import com.picnicml.doddlemodel.linear.LogisticRegression.ev
import org.scalactic.{Equality, TolerantNumerics}
import org.scalatest.{FlatSpec, Matchers}

class LogisticRegressionTest extends FlatSpec with Matchers with TestUtils {

  implicit val doubleTolerance: Equality[Double] = TolerantNumerics.tolerantDoubleEquality(1e-4)

  "Logistic regression" should "calculate the value of the loss function" in {
    val w = DenseVector(1.0, 2.0, 3.0)
    val x = DenseMatrix((3.0, 1.0, 2.0), (-1.0, -2.0, 2.0))
    val y = DenseVector(1.0, 0.0)

    val model = LogisticRegression(lambda = 1)
    ev.lossStateless(model, w, x, y) shouldEqual 7.1566391945397703
  }

  it should "calculate the gradient of the loss function wrt. to model parameters" in {
    for (_ <- 1 to 1000) {
      val w = DenseVector.rand[Double](5)
      val x = DenseMatrix.rand[Double](10, 5)
      val y = convert(round(DenseVector.rand[Double](10)), Double)
      testGrad(w, x, y)
    }

    def testGrad(w: RealVector, x: Features, y: Target) = {
      val model = LogisticRegression(lambda = 0.5)
      breezeEqual(
        gradApprox(w => ev.lossStateless(model, w, x, y), w),
        ev.lossGradStateless(model, w, x, y)) shouldEqual true
    }
  }


  it should "prevent the usage of negative L2 regularization strength" in {
    an [IllegalArgumentException] shouldBe thrownBy(LogisticRegression(lambda = -0.5))
  }

  it should "throw an exception if fitting a model on a dataset with more than two classes" in {
    val x = DenseMatrix((3.0, 1.0, 2.0), (-1.0, -2.0, 2.0), (3.0, 1.0, 2.0))
    val y = DenseVector(1.0, 0.0, 2.0)
    val model = LogisticRegression()

    an [IllegalArgumentException] shouldBe thrownBy(ev.fit(model, x, y))
  }
}
