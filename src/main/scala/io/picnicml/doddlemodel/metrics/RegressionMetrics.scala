package io.picnicml.doddlemodel.metrics

import breeze.linalg._
import breeze.numerics._
import breeze.stats.variance
import io.picnicml.doddlemodel.data.Target

object RegressionMetrics {

  /** Root mean squared error. */
  object Rmse extends Metric {

    override lazy val higherValueIsBetter: Boolean = false

    override def calculateValueSafe(y: Target, yPred: Target): Double = sqrt(mse(y, yPred))

    override def toString: String = "RMSE"
  }

  /** Mean squared error. */
  object Mse extends Metric {

    override lazy val higherValueIsBetter: Boolean = false

    override def calculateValueSafe(y: Target, yPred: Target): Double = {
      val diff = y - yPred
      (diff.t * diff) / y.length.toDouble
    }

    override def toString: String = "MSE"
  }

  /** Mean absolute error. */
  object Mae extends Metric {

    override lazy val higherValueIsBetter: Boolean = false

    override def calculateValueSafe(y: Target, yPred: Target): Double = sum(abs(y - yPred)) / y.length.toDouble

    override def toString: String = "MAE"
  }

  /** Explained variance. */
  object ExplainedVariance extends Metric {

    override lazy val higherValueIsBetter: Boolean = true

    override def calculateValueSafe(y: Target, yPred: Target): Double = 1.0 - variance(y - yPred) / variance(y)

    override def toString: String = "explained variance"
  }
}
