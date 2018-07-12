package com.picnicml.doddlemodel

import com.picnicml.doddlemodel.metrics.ClassificationMetrics._
import com.picnicml.doddlemodel.metrics.RankingMetrics._
import com.picnicml.doddlemodel.metrics.RegressionMetrics._

package object metrics {

  // regression metrics
  lazy val rmse: Metric = Rmse
  lazy val mae: Metric = Mae
  lazy val explainedVariance: Metric = ExplainedVariance

  // classification metrics
  lazy val accuracy: Metric = Accuracy

  // ranking metrics
  lazy val auc: Metric = Auc
}
