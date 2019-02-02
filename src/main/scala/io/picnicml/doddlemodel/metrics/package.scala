package io.picnicml.doddlemodel

import io.picnicml.doddlemodel.metrics.ClassificationMetrics._
import io.picnicml.doddlemodel.metrics.RankingMetrics._
import io.picnicml.doddlemodel.metrics.RegressionMetrics._

package object metrics {

  // regression metrics
  lazy val mse: Metric = Mse
  lazy val rmse: Metric = Rmse
  lazy val mae: Metric = Mae
  lazy val explainedVariance: Metric = ExplainedVariance

  // classification metrics
  lazy val accuracy: Metric = Accuracy
  lazy val precision: Metric = Precision
  lazy val recall: Metric = Recall
  lazy val f1Score: Metric = F1Score
  lazy val hammingLoss: Metric = HammingLoss

  // ranking metrics
  lazy val auc: Metric = Auc
}
