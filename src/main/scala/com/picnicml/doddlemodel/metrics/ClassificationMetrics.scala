package com.picnicml.doddlemodel.metrics

import breeze.linalg.sum
import com.picnicml.doddlemodel.data.Target

object ClassificationMetrics {

  /** Classification accuracy. */
  object Accuracy extends Metric {

    lazy val higherValueIsBetter: Boolean = true

    override def apply(y: Target, yPred: Target): Double = {
      val correct = (y :== yPred).map(x => if (x) 1 else 0)
      sum(correct) / correct.length.toDouble
    }
  }
}
