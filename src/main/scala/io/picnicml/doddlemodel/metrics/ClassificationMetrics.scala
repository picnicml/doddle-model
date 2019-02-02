package io.picnicml.doddlemodel.metrics

import io.picnicml.doddlemodel.data.{Target, numberOfTargetClasses}

object ClassificationMetrics {

  /** Classification accuracy. */
  object Accuracy extends Metric {

    override lazy val higherValueIsBetter: Boolean = true

    override def apply(y: Target, yPred: Target): Double =
      (y :== yPred).activeSize / y.length.toDouble
  }

  /** Positive predictive value. */
  object Precision extends Metric {

    override lazy val higherValueIsBetter: Boolean = true

    override def apply(y: Target, yPred: Target): Double = {
      require(numberOfTargetClasses(y) == 2, "Precision is defined for a binary classification task")
      val yPredPositive = yPred :== 1.0
      val yPositive = y :== 1.0

      val numTp = (yPredPositive &:& yPositive).activeSize.toDouble
      numTp / (numTp + (yPredPositive &:& !yPositive).activeSize.toDouble)
    }
  }

  /** Sensitivity. */
  object Recall extends Metric {

    override lazy val higherValueIsBetter: Boolean = true

    override def apply(y: Target, yPred: Target): Double = {
      require(numberOfTargetClasses(y) == 2, "Recall is defined for a binary classification task")
      val yPredPositive = yPred :== 1.0
      val yPositive = y :== 1.0

      val numTp = (yPredPositive &:& yPositive).activeSize.toDouble
      numTp / (numTp + (!yPredPositive &:& yPositive).activeSize.toDouble)
    }
  }

  /** F1 score. */
  object F1Score extends Metric {

    override lazy val higherValueIsBetter: Boolean = true

    override def apply(y: Target, yPred: Target): Double = {
      require(y.length == yPred.length, "Target vectors need to be of equal length")
      require(numberOfTargetClasses(y) == 2, "F1 score is defined for a binary classification task")
      val prec = precision(y, yPred)
      val rec = recall(y, yPred)

      2.0 * (prec * rec) / (prec + rec)
    }
  }

  /** Hamming loss. */
  object HammingLoss extends Metric {

    override lazy val higherValueIsBetter: Boolean = false

    override def apply(y: Target, yPred: Target): Double = {
      require(y.length == yPred.length, "Target vectors need to be of equal length")
      1.0 - accuracy(y, yPred)
    }
  }
}
