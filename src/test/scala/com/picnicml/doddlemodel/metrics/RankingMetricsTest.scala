package com.picnicml.doddlemodel.metrics

import breeze.linalg.DenseVector
import com.picnicml.doddlemodel.metrics.RankingMetrics.rocCurve
import org.scalatest.{FlatSpec, Matchers}

class RankingMetricsTest extends FlatSpec with Matchers {

  "Ranking metrics" should "compute a correct ROC curve" in {
    val y = DenseVector(1.0, 0.0, 0.0, 0.0, 1.0)
    val yPredProba = DenseVector(1.0, 0.0, 0.0, 0.0, 1.0)
    val roc = rocCurve(y, yPredProba)
    // todo
  }

  they should "calculate a correct AUC value" in {
    // todo
  }
}
