package com.picnicml.doddlemodel.metrics

import breeze.linalg.DenseVector
import org.scalatest.{FlatSpec, Matchers}

class RegressionMetricsTest extends FlatSpec with Matchers {

  "Regression metrics" should "calculate a correct rmse value" in {
    val y = DenseVector(1.0, 4.1, 2.2, 5.1, 9.6)
    val yPred = DenseVector(1.2, 1.4, 8.2, 3.1, 9.6)

    rmse(y, yPred) shouldEqual 3.076686529368892
  }
}
