package io.picnicml.doddlemodel.metrics

import breeze.linalg.DenseVector
import org.scalatest.{FlatSpec, Matchers}

class RankingMetricsTest extends FlatSpec with Matchers {

  "Ranking metrics" should "calculate the AUC value" in {
    val y = DenseVector(1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f)
    val yPredProba = DenseVector(
      0.6346f, 0.0742f, 0.4324f, 0.9911f, 0.7245f, 0.4751f, 0.5112f, 0.0311f, 0.7641f, 0.6612f, 0.0134f
    )

    val aucScore = auc(y, yPredProba)
    aucScore shouldBe 0.733333333333333f +- 1e-15f
  }
}
