package com.picnicml.doddlemodel.metrics

import breeze.numerics._
import breeze.linalg._
import breeze.stats.variance
import com.picnicml.doddlemodel.data.Target

object RegressionMetrics {

  /** Root mean squared error. */
  object Rmse extends Metric {

    lazy val higherValueIsBetter: Boolean = false

    override def apply(y: Target, yPred: Target): Double = {
      val d = y - yPred
      sqrt((d.t * d) / y.length.toDouble)
    }
  }

  /** Mean absolute error */
  object Mae extends Metric {

    lazy val higherValueIsBetter: Boolean = false

    override def apply(y: Target, yPred: Target): Double = {
      val d = y - yPred
      sum(abs(d)) / y.length.toDouble
    }
  }


  /** Explained variance */
  object ExplainedVariance extends Metric {

    lazy val higherValueIsBetter: Boolean = true

    override def apply(y: Target, yPred: Target): Double = {
      val d = y - yPred
      1 - variance(d)/variance(y)
    }
  }
}
