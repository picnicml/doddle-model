package com.doddle.metrics

import breeze.linalg.DenseVector
import com.doddle.metrics.RegressionMetrics.rmse
import org.scalatest.{FlatSpec, Matchers}

class RegressionMetricsTest extends FlatSpec with Matchers {

  "Regression metrics" should "calculate a correct rmse value" in {
    val y = DenseVector(1.0, 4.1, 2.2, 5.1, 9.6)
    val yPred = DenseVector(1.2, 1.4, 8.2, 3.1, 9.6)

    rmse(y, yPred) shouldEqual 3.076686529368892
  }
}
