package com.doddle.metrics

import breeze.numerics.sqrt
import com.doddle.TypeAliases.RealVector

object RegressionMetrics {

  /** Root mean squared error. */
  def rmse(y: RealVector, yPred: RealVector): Double = {
    val d = y - yPred
    sqrt((d.t * d) / y.length.toDouble)
  }
}
