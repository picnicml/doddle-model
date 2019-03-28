package io.picnicml.doddlemodel.metrics

import breeze.linalg.DenseVector
import org.scalatest.{FlatSpec, Matchers}

class RegressionMetricsTest extends FlatSpec with Matchers {

  "Regression metrics" should "calculate the rmse value" in {
    val y = DenseVector(1.0, 4.1, 2.2, 5.1, 9.6)
    val yPred = DenseVector(1.2, 1.4, 8.2, 3.1, 9.6)

    rmse(y, yPred) shouldEqual 3.076686529368892
  }

  they should "calculate the mse value" in {
    val y = DenseVector(1.0, 4.1, 2.2, 5.1, 9.6)
    val yPred = DenseVector(1.2, 1.4, 8.2, 3.1, 9.6)

    mse(y, yPred) shouldEqual 9.466 +- 0.001
  }

  they should "calculate the mae value" in {
    val y = DenseVector(1.0, 4.1, 2.2, 5.1, 9.6)
    val yPred = DenseVector(1.2, 1.9, 2.8, 4.1, 10.6)

    assert(mae(y, yPred) === 1.0 +- 0.0000001)
  }

  they should "calculate the explained variance score" in {
    val y = DenseVector(1.0, 4.1, 2.2, 5.1, 9.6)
    val yPred = DenseVector(2.2, 2.9, 0.0, 6.1, 10.8)

    assert(explainedVariance(y, yPred) === 0.769195820081781 +- 0.0000001)
  }
}
