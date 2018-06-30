package com.picnicml.doddlemodel.metrics

import breeze.linalg.{DenseMatrix, linspace}
import com.picnicml.doddlemodel.data.{RealVector, Target, numberOfTargetClasses}

object RankingMetrics {

  /** Area under the ROC-curve. **/
  object Auc extends Metric {

    override lazy val higherValueIsBetter: Boolean = true

    override def apply(y: Target, yPredProba: RealVector): Double = {
      val roc = rocCurve(y, yPredProba)
      // todo: integrate
      ???
    }
  }

  case class RocCurve(fpr: RealVector, tpr: RealVector, thresholds: RealVector)

  /** Receiver operating characteristic curve (ROC-curve). **/
  def rocCurve(y: Target, yPredProba: RealVector, length: Int = 100): RocCurve = {
    require(numberOfTargetClasses(y) == 2, "ROC-curve is defined for a binary classification task")

    val yPositive = y :== 1.0
    val yNegative = !yPositive

    def fprTpr(threshold: Double): Array[Double] = {
      val yPredPositive = yPredProba >:> threshold
      val numTp = (yPredPositive &:& yPositive).activeSize
      val numFp = (yPredPositive &:& yNegative).activeSize
      Array(numFp / yNegative.activeSize.toDouble, numTp / yPositive.activeSize.toDouble)
    }

    val thresholds = linspace(1.0, 0.0, length)
    val coordinates = DenseMatrix(thresholds.toArray.map(threshold => fprTpr(threshold)):_*)
    RocCurve(coordinates(::, 0), coordinates(::, 1), thresholds)
  }
}
