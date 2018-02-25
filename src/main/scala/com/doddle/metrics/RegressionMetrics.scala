package com.doddle.metrics

import breeze.numerics.sqrt
import com.doddle.data.DataTypes.Target

object RegressionMetrics {

  /** Root mean squared error. */
  def rmse(y: Target[Double], yPred: Target[Double]): Double = {
    val d = y - yPred
    sqrt((d.t * d) / y.length.toDouble)
  }
}
