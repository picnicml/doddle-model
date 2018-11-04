package com.picnicml.doddlemodel.linear

import breeze.linalg.{DenseMatrix, DenseVector}
import com.picnicml.doddlemodel.TestingUtils
import com.picnicml.doddlemodel.data.{Features, RealVector, Target}
import com.picnicml.doddlemodel.linear.SoftmaxClassifier.ev
import org.scalactic.{Equality, TolerantNumerics}
import org.scalatest.{FlatSpec, Matchers}

class SoftmaxClassifierTest extends FlatSpec with Matchers with TestingUtils {

  implicit val doubleTolerance: Equality[Double] = TolerantNumerics.tolerantDoubleEquality(1e-4)

  "Softmax classifier" should "calculate the value of the loss function" in {
    val w = DenseVector(1.0, 2.0, 3.0, 4.0, 5.0, 1.0)
    val x = DenseMatrix((3.0, 1.0, 2.0), (-1.0, -2.0, 2.0), (-2.0, 1.0, 0.0))
    val y = DenseVector(1.0, 0.0, 2.0)

    val model = ev.copy(SoftmaxClassifier(lambda = 1), numClasses = 3)
    ev.lossStateless(model, w, x, y) shouldEqual 19.843778223530194
  }

  it should "calculate the gradient of the loss function wrt. to model parameters" in {
    for (_ <- 1 to 1000) {
      val w = DenseVector.rand[Double](5 * 9)
      val x = DenseMatrix.rand[Double](10, 5)
      val y = DenseVector.rangeD(0, 10)
      testGrad(w, x, y)
    }

    def testGrad(w: RealVector, x: Features, y: Target) = {
      val model = ev.copy(SoftmaxClassifier(lambda = 0.5), numClasses = 10)
      breezeEqual(
        gradApprox(w => ev.lossStateless(model, w, x, y), w),
        ev.lossGradStateless(model, w, x, y)) shouldEqual true
    }
  }

  it should "prevent the usage of negative L2 regularization strength" in {
    an [IllegalArgumentException] shouldBe thrownBy(SoftmaxClassifier(lambda = -0.5))
  }
}
