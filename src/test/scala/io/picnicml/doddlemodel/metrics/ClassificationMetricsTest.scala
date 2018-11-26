package io.picnicml.doddlemodel.metrics

import breeze.linalg.DenseVector
import org.scalatest.{FlatSpec, Matchers}

class ClassificationMetricsTest extends FlatSpec with Matchers {

  "Classification metrics" should "calculate a correct classification accuracy value" in {
    val y = DenseVector(1.0, 0.0, 0.0, 0.0, 1.0)
    val yPred = DenseVector(1.0, 1.0, 0.0, 1.0, 0.0)

    accuracy(y, yPred) shouldEqual 0.4
  }

  they should "calculate a correct precision value" in {
    val y = DenseVector(1.0, 0.0, 0.0, 0.0, 1.0)
    val yPred = DenseVector(1.0, 1.0, 0.0, 1.0, 0.0)

    precision(y, yPred) shouldBe 0.3333333333333333
  }

  they should "calculate a correct recall value" in {
    val y = DenseVector(1.0, 0.0, 0.0, 0.0, 1.0)
    val yPred = DenseVector(1.0, 1.0, 0.0, 1.0, 0.0)

    recall(y, yPred) shouldBe 0.5
  }
}
