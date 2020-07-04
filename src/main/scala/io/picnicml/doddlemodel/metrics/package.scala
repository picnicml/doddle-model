package io.picnicml.doddlemodel

import io.picnicml.doddlemodel.metrics.ClassificationMetrics._
import io.picnicml.doddlemodel.metrics.RankingMetrics._
import io.picnicml.doddlemodel.metrics.RegressionMetrics._

/** Provides various evaluation metrics for prediction tasks. */
package object metrics {

  // regression metrics
  /** @see [[io.picnicml.doddlemodel.metrics.RegressionMetrics.Mse]] */
  lazy val mse: Metric = Mse

  /** @see [[io.picnicml.doddlemodel.metrics.RegressionMetrics.Rmse]] */
  lazy val rmse: Metric = Rmse

  /** @see [[io.picnicml.doddlemodel.metrics.RegressionMetrics.Mae]] */
  lazy val mae: Metric = Mae

  /** @see [[io.picnicml.doddlemodel.metrics.RegressionMetrics.ExplainedVariance]] */
  lazy val explainedVariance: Metric = ExplainedVariance

  // classification metrics
  /** @see [[io.picnicml.doddlemodel.metrics.ClassificationMetrics.Accuracy]] */
  lazy val accuracy: Metric = Accuracy

  /** @see [[io.picnicml.doddlemodel.metrics.ClassificationMetrics.Precision]] */
  lazy val precision: Metric = Precision

  /** @see [[io.picnicml.doddlemodel.metrics.ClassificationMetrics.Recall]] */
  lazy val recall: Metric = Recall

  /** @see [[io.picnicml.doddlemodel.metrics.ClassificationMetrics.F1Score]] */
  lazy val f1Score: Metric = F1Score

  /** @see [[io.picnicml.doddlemodel.metrics.ClassificationMetrics.HammingLoss]] */
  lazy val hammingLoss: Metric = HammingLoss

  // ranking metrics
  /** @see [[io.picnicml.doddlemodel.metrics.RankingMetrics.Auc]] */
  lazy val auc: Metric = Auc
}
