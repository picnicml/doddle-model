package com.picnicml.doddlemodel.metrics

import com.picnicml.doddlemodel.data.Target

object ClassificationMetrics {

  /** Classification accuracy. */
  object Accuracy extends Metric {

    override lazy val higherValueIsBetter: Boolean = true

    override def apply(y: Target, yPred: Target): Double =
      (y :== yPred).activeSize / y.length.toDouble
  }
}
