package io.picnicml.doddlemodel.metrics

import breeze.linalg._
import breeze.numerics._
import breeze.stats.variance
import io.picnicml.doddlemodel.data.Target

object RegressionMetrics {

  /** Root mean squared error. */
  object Rmse extends Metric {

    override lazy val higherValueIsBetter: Boolean = false

    override def apply(y: Target, yPred: Target): Double = {
      val d = y - yPred
      sqrt((d.t * d) / y.length.toDouble)
    }
  }

  /** Mean absolute error. */
  object Mae extends Metric {

    override lazy val higherValueIsBetter: Boolean = false

    override def apply(y: Target, yPred: Target): Double = sum(abs(y - yPred)) / y.length.toDouble
  }


  /** Explained variance. */
  object ExplainedVariance extends Metric {

    override lazy val higherValueIsBetter: Boolean = true

    override def apply(y: Target, yPred: Target): Double = 1.0 - variance(y - yPred) / variance(y)
  }
}
