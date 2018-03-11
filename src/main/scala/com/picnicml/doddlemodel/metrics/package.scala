package com.picnicml.doddlemodel

import com.picnicml.doddlemodel.data.Target

package object metrics {

  type Metric = (Target, Target) => Double

  /** Regression metrics. */
  def rmse(y: Target, yPred: Target): Double = RegressionMetrics.rmse(y: Target, yPred: Target)

  /** Classification metrics. */
  def accuracy(y: Target, yPred: Target): Double = ClassificationMetrics.accuracy(y, yPred)
}
