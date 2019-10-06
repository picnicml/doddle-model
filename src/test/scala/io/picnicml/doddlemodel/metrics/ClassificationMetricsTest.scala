package io.picnicml.doddlemodel.metrics

import breeze.linalg.DenseVector
import org.scalatest.{FlatSpec, Matchers}

class ClassificationMetricsTest extends FlatSpec with Matchers {

  "Classification metrics" should "calculate the classification accuracy value" in {
    val y = DenseVector(1.0f, 0.0f, 0.0f, 0.0f, 1.0f)
    val yPred = DenseVector(1.0f, 1.0f, 0.0f, 1.0f, 0.0f)

    accuracy(y, yPred) shouldEqual 0.4f
  }

  they should "calculate the precision value" in {
    val y = DenseVector(1.0f, 0.0f, 0.0f, 0.0f, 1.0f)
    val yPred = DenseVector(1.0f, 1.0f, 0.0f, 1.0f, 0.0f)

    precision(y, yPred) shouldBe 0.3333333333333333f
  }

  they should "calculate the recall value" in {
    val y = DenseVector(1.0f, 0.0f, 0.0f, 0.0f, 1.0f)
    val yPred = DenseVector(1.0f, 1.0f, 0.0f, 1.0f, 0.0f)

    recall(y, yPred) shouldBe 0.5f
  }

  they should "calculate the F1 score value" in {
    val y = DenseVector(1.0f, 0.0f, 0.0f, 0.0f, 1.0f)
    val yPred = DenseVector(1.0f, 1.0f, 0.0f, 1.0f, 0.0f)

    f1Score(y, yPred) shouldBe 0.4f
  }

  they should "calculate the Hamming loss value" in {
    val y = DenseVector(1.0f, 0.0f, 0.0f, 0.0f, 1.0f)
    val yPred = DenseVector(1.0f, 1.0f, 0.0f, 1.0f, 0.0f)

    // 3 out of 5 miss-classifications
    hammingLoss(y, yPred) shouldBe 0.6f
  }
}
