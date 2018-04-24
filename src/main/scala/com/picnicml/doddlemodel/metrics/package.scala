package com.picnicml.doddlemodel

import com.picnicml.doddlemodel.metrics.ClassificationMetrics.Accuracy
import com.picnicml.doddlemodel.metrics.RegressionMetrics.Rmse

package object metrics {

  // regression metrics
  lazy val rmse: Metric = Rmse

  // classification metrics
  lazy val accuracy: Metric = Accuracy
}
