package com.picnicml.doddlemodel

import com.picnicml.doddlemodel.metrics.ClassificationMetrics.Accuracy
import com.picnicml.doddlemodel.metrics.RegressionMetrics._

package object metrics {

  // regression metrics
  lazy val rmse: Metric = Rmse
  lazy val mae: Metric = Mae
  lazy val explainedVariance: Metric = ExplainedVariance

  // classification metrics
  lazy val accuracy: Metric = Accuracy
}
