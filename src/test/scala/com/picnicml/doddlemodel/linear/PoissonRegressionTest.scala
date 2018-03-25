package com.picnicml.doddlemodel.linear

import breeze.linalg.{DenseMatrix, DenseVector, convert}
import breeze.stats.distributions.Rand
import com.picnicml.doddlemodel.TestUtils
import com.picnicml.doddlemodel.data.{Features, RealVector, Target}
import org.scalactic.{Equality, TolerantNumerics}
import org.scalatest.{FlatSpec, Matchers}

class PoissonRegressionTest extends FlatSpec with Matchers with TestUtils {

  implicit val doubleTolerance: Equality[Double] = TolerantNumerics.tolerantDoubleEquality(1e-4)

  "Poisson regression" should "calculate the value of the loss function" in {
    val w = DenseVector(1.0, 2.0, 3.0)
    val x = DenseMatrix((3.0, 1.0, 2.0), (-1.0, -2.0, 2.0))
    val y = DenseVector(3.0, 4.0)

    val model = PoissonRegression(lambda = 1)
    model.loss(w, x, y) shouldEqual 29926.429998513137
  }

  it should "calculate the gradient of the loss function wrt. to model parameters" in {
    for (_ <- 1 to 1000) {
      val w = DenseVector.rand[Double](5)
      val x = DenseMatrix.rand[Double](10, 5)
      val y = convert(DenseVector.rand(10, rand = Rand.randInt(1e6.toInt)), Double)
      testGrad(w, x, y)
    }

    def testGrad(w: RealVector, x: Features, y: Target) = {
      val model = PoissonRegression(lambda = 0.5)
      breezeEqual(gradApprox(w => model.loss(w, x, y), w), model.lossGrad(w, x, y)) shouldEqual true
    }
  }

  it should "prevent the usage of negative L2 regularization strength" in {
    an [IllegalArgumentException] shouldBe thrownBy(PoissonRegression(lambda = -0.5))
  }

  it should "throw an exception if fitting a model on a dataset that is not count data" in {
    val x = DenseMatrix((3.0, 1.0, 2.0), (-1.0, -2.0, 2.0), (3.0, 1.0, 2.0))
    val y = DenseVector.rand[Double](3)
    val model = PoissonRegression()

    an [IllegalArgumentException] shouldBe thrownBy(model.fit(x, y))
  }
}
