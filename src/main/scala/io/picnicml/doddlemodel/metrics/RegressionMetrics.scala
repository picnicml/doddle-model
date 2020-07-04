package io.picnicml.doddlemodel.metrics

import breeze.linalg._
import breeze.numerics._
import breeze.stats.variance
import io.picnicml.doddlemodel.data.Target

object RegressionMetrics {

  /** Root mean squared error - defined as the square root of mean squared error.
    *
    * @see [[https://en.wikipedia.org/wiki/Root-mean-square_deviation]]q
    * */
  object Rmse extends Metric {

    override lazy val higherValueIsBetter: Boolean = false

    override def calculateValueSafe(y: Target, yPred: Target): Float = sqrt(mse(y, yPred))

    override def toString: String = "RMSE"
  }

  /** Mean squared error - defined as the average of the squares of error.
    *
    * @see [[https://en.wikipedia.org/wiki/Mean_squared_error]]
    * */
  object Mse extends Metric {

    override lazy val higherValueIsBetter: Boolean = false

    override def calculateValueSafe(y: Target, yPred: Target): Float = {
      val diff = y - yPred
      (diff.t * diff) / y.length.toFloat
    }

    override def toString: String = "MSE"
  }

  /** Mean absolute error - defined as the average of absolute error.
    *
    * @see [[https://en.wikipedia.org/wiki/Mean_absolute_error]]
    * */
  object Mae extends Metric {

    override lazy val higherValueIsBetter: Boolean = false

    override def calculateValueSafe(y: Target, yPred: Target): Float = sum(abs(y - yPred)) / y.length.toFloat

    override def toString: String = "MAE"
  }


  /** Explained variance - measures the proportion of variance in dataset that is captured by the model.
    *
    * @see [[https://en.wikipedia.org/wiki/Explained_variation]]
    * */
  object ExplainedVariance extends Metric {

    override lazy val higherValueIsBetter: Boolean = true

    override def calculateValueSafe(y: Target, yPred: Target): Float =
      1.0f - variance(y - yPred).toFloat / variance(y).toFloat

    override def toString: String = "explained variance"
  }
}
