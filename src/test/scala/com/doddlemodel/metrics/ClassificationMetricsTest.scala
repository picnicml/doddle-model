package com.doddlemodel.metrics

import breeze.linalg.DenseVector
import com.doddlemodel.metrics.ClassificationMetrics.accuracy
import org.scalatest.{FlatSpec, Matchers}

class ClassificationMetricsTest extends FlatSpec with Matchers {

  "Classification metrics" should "calculate a correct classification accuracy value" in {
    val y = DenseVector(1.0, 0.0, 0.0, 0.0, 1.0)
    val yPred = DenseVector(1.0, 1.0, 0.0, 1.0, 0.0)

    accuracy(y, yPred) shouldEqual 0.4
  }
}
