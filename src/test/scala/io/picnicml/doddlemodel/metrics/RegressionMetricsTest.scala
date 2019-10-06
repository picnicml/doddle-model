package io.picnicml.doddlemodel.metrics

import breeze.linalg.DenseVector
import org.scalatest.{FlatSpec, Matchers}

class RegressionMetricsTest extends FlatSpec with Matchers {

  "Regression metrics" should "calculate the rmse value" in {
    val y = DenseVector(1.0f, 4.1f, 2.2f, 5.1f, 9.6f)
    val yPred = DenseVector(1.2f, 1.4f, 8.2f, 3.1f, 9.6f)

    rmse(y, yPred) shouldEqual 3.076686529368892f
  }

  they should "calculate the mse value" in {
    val y = DenseVector(1.0f, 4.1f, 2.2f, 5.1f, 9.6f)
    val yPred = DenseVector(1.2f, 1.4f, 8.2f, 3.1f, 9.6f)

    mse(y, yPred) shouldEqual 9.466f +- 0.001f
  }

  they should "calculate the mae value" in {
    val y = DenseVector(1.0f, 4.1f, 2.2f, 5.1f, 9.6f)
    val yPred = DenseVector(1.2f, 1.9f, 2.8f, 4.1f, 10.6f)

    assert(mae(y, yPred) === 1.0f +- 0.0000001f)
  }

  they should "calculate the explained variance score" in {
    val y = DenseVector(1.0f, 4.1f, 2.2f, 5.1f, 9.6f)
    val yPred = DenseVector(2.2f, 2.9f, 0.0f, 6.1f, 10.8f)

    assert(explainedVariance(y, yPred) === 0.769195820081781f +- 0.0000001f)
  }
}
