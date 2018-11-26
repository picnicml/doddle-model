package io.picnicml.doddlemodel.metrics

import breeze.linalg.DenseVector
import org.scalatest.{FlatSpec, Matchers}

class RankingMetricsTest extends FlatSpec with Matchers {

  "Ranking metrics" should "calculate a correct AUC value" in {
    val y = DenseVector(1.0, 1.0, 0.0, 0.0, 1.0, 0.0, 1.0, 0.0, 1.0, 1.0, 0.0)
    val yPredProba = DenseVector(0.6346, 0.0742, 0.4324, 0.9911, 0.7245, 0.4751, 0.5112, 0.0311, 0.7641, 0.6612, 0.0134)

    val aucScore = auc(y, yPredProba)
    aucScore shouldBe 0.733333333333333 +- 1e-15
  }
}
