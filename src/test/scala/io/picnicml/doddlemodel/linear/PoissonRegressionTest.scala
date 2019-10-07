package io.picnicml.doddlemodel.linear

import breeze.linalg.{DenseMatrix, DenseVector, convert}
import breeze.stats.distributions.Rand
import io.picnicml.doddlemodel.TestingUtils
import io.picnicml.doddlemodel.data.{Features, RealVector, Target}
import io.picnicml.doddlemodel.linear.PoissonRegression.ev
import org.scalactic.{Equality, TolerantNumerics}
import org.scalatest.{FlatSpec, Matchers}

class PoissonRegressionTest extends FlatSpec with Matchers with TestingUtils {

  implicit val tolerance: Equality[Float] = TolerantNumerics.tolerantFloatEquality(1e-2f)

  "Poisson regression" should "calculate the value of the loss function" in {
    val w = DenseVector(1.0f, 2.0f, 3.0f)
    val x = DenseMatrix(
      List(3.0f, 1.0f, 2.0f),
      List(-1.0f, -2.0f, 2.0f)
    )
    val y = DenseVector(3.0f, 4.0f)

    val model = PoissonRegression(lambda = 1.0f)
    ev.lossStateless(model, w, x, y) shouldEqual 29926.429998513137f
  }

  it should "calculate the gradient of the loss function wrt. to model parameters" in {
    for (_ <- 1 to 1000) {
      val w = DenseVector.rand[Float](5, rand = randomUniform)
      val x = DenseMatrix.rand[Float](10, 5, rand = randomUniform)
      val y = convert(DenseVector.rand(10, rand = Rand.randInt(20)), Float)
      testGrad(w, x, y)
    }

    def testGrad(w: RealVector, x: Features, y: Target) = {
      val model = PoissonRegression(lambda = 0.5f)
      breezeEqual(
        gradApprox(w => ev.lossStateless(model, w, x, y), w),
        ev.lossGradStateless(model, w, x, y)
      ) shouldEqual true
    }
  }

  it should "prevent the usage of negative L2 regularization strength" in {
    an [IllegalArgumentException] shouldBe thrownBy(PoissonRegression(lambda = -0.5f))
  }

  it should "throw an exception if fitting a model on a dataset that is not count data" in {
    val x = DenseMatrix(
      List(3.0f, 1.0f, 2.0f),
      List(-1.0f, -2.0f, 2.0f),
      List(3.0f, 1.0f, 2.0f)
    )
    val y = DenseVector.rand[Float](3, rand = randomUniform)
    val model = PoissonRegression()

    an [IllegalArgumentException] shouldBe thrownBy(ev.fit(model, x, y))
  }
}
