package io.picnicml.doddlemodel.metrics

import breeze.linalg.{DenseMatrix, convert, linspace, max, min}
import io.picnicml.doddlemodel.data.{RealVector, Target, numberOfTargetClasses}

import scala.collection.compat.immutable.ArraySeq

object RankingMetrics {

  /** Area under the ROC-curve. **/
  object Auc extends Metric {

    override lazy val higherValueIsBetter: Boolean = true

    override def calculateValueSafe(y: Target, yPredProba: Target): Float = {
      val roc = rocCurve(y, yPredProba)
      // integrate with the trapezoid rule
      (1 until roc.thresholds.length).foldLeft(0.0f) { case (integral, index) =>
        integral + ((roc.tpr(index - 1) + roc.tpr(index)) * 0.5f * (roc.fpr(index) - roc.fpr(index - 1)))
      }
    }

    override def toString: String = "AUC"
  }

  case class RocCurve(fpr: RealVector, tpr: RealVector, thresholds: RealVector)

  /** Receiver operating characteristic curve (ROC-curve).
    *
    * @param length the number of thresholds to take into account, i.e. the number of coordinates returned
    */
  def rocCurve(y: Target, yPredProba: RealVector, length: Int = 30): RocCurve = {
    require(length >= 5, "Number of points of the ROC-curve must be at least 3")
    require(numberOfTargetClasses(y) == 2, "ROC-curve is defined for a binary classification task")
    require(min(yPredProba) >= 0 && max(yPredProba) <= 1, "Currently ROC-curve is only defined for probability scores")

    val yPositive = y :== 1.0f
    val yNegative = !yPositive

    def fprTpr(threshold: Float): Array[Float] = {
      val yPredPositive =
        if (threshold == 0.0f) {
          // predict 1.0 if predicted probability is 0.0 to obtain coordinate (1, 1)
          (yPredProba >:> threshold) |:| (yPredProba :== threshold)
        }
        else {
          yPredProba >:> threshold
        }

      val numTp = (yPredPositive &:& yPositive).activeSize
      val numFp = (yPredPositive &:& yNegative).activeSize
      Array(numFp / yNegative.activeSize.toFloat, numTp / yPositive.activeSize.toFloat)
    }

    val thresholds = convert(linspace(1.0, 0.0, length), Float)
    val coordinates = DenseMatrix(ArraySeq.unsafeWrapArray(thresholds.toArray.map(threshold => fprTpr(threshold))):_*)
    RocCurve(coordinates(::, 0), coordinates(::, 1), thresholds)
  }
}
