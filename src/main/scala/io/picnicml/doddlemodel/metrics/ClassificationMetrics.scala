package io.picnicml.doddlemodel.metrics

import io.picnicml.doddlemodel.data.{numberOfTargetClasses, Target}

object ClassificationMetrics {

  /** Classification accuracy. */
  object Accuracy extends Metric {

    override lazy val higherValueIsBetter: Boolean = true

    override def calculateValueSafe(y: Target, yPred: Target): Double =
      (y :== yPred).activeSize / y.length.toDouble

    override def toString: String = "accuracy"
  }

  /** Positive predictive value. */
  object Precision extends Metric {

    override lazy val higherValueIsBetter: Boolean = true

    override def checkInput(y: Target, yPred: Target): Unit = {
      super.checkInput(y, yPred)
      require(numberOfTargetClasses(y) == 2, "Precision is defined for a binary classification task")
    }

    override def calculateValueSafe(y: Target, yPred: Target): Double = {
      val yPredPositive = yPred :== 1.0
      val yPositive = y :== 1.0

      val numTp = (yPredPositive &:& yPositive).activeSize.toDouble
      numTp / (numTp + (yPredPositive &:& !yPositive).activeSize.toDouble)
    }

    override def toString: String = "precision"
  }

  /** Sensitivity. */
  object Recall extends Metric {

    override lazy val higherValueIsBetter: Boolean = true

    override def checkInput(y: Target, yPred: Target): Unit = {
      super.checkInput(y, yPred)
      require(numberOfTargetClasses(y) == 2, "Recall is defined for a binary classification task")
    }

    override def calculateValueSafe(y: Target, yPred: Target): Double = {
      val yPredPositive = yPred :== 1.0
      val yPositive = y :== 1.0

      val numTp = (yPredPositive &:& yPositive).activeSize.toDouble
      numTp / (numTp + (!yPredPositive &:& yPositive).activeSize.toDouble)
    }

    override def toString: String = "recall"
  }

  /** F1 score. */
  object F1Score extends Metric {

    override lazy val higherValueIsBetter: Boolean = true

    override def checkInput(y: Target, yPred: Target): Unit = {
      super.checkInput(y, yPred)
      require(numberOfTargetClasses(y) == 2, "F1 score is defined for a binary classification task")
    }

    override def calculateValueSafe(y: Target, yPred: Target): Double = {
      val prec = precision(y, yPred)
      val rec = recall(y, yPred)

      2.0 * (prec * rec) / (prec + rec)
    }

    override def toString: String = "F1 score"
  }

  /** Hamming loss. */
  object HammingLoss extends Metric {

    override lazy val higherValueIsBetter: Boolean = false

    override def calculateValueSafe(y: Target, yPred: Target): Double = 1.0 - accuracy(y, yPred)

    override def toString: String = "Hamming loss"
  }
}
