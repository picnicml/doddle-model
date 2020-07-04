package io.picnicml.doddlemodel.metrics

import io.picnicml.doddlemodel.data.{Target, numberOfTargetClasses}

object ClassificationMetrics {

  /** Classification accuracy - measures the proportion of correctly classified examples among all examples. */
  object Accuracy extends Metric {

    override lazy val higherValueIsBetter: Boolean = true

    override def calculateValueSafe(y: Target, yPred: Target): Float =
      (y :== yPred).activeSize / y.length.toFloat

    override def toString: String = "accuracy"
  }

  /** Precision (positive predictive value) - measures the proportion of correctly classified positive examples
    * (true positives) among all examples classified as positive.
    *
    * @note Only defined for a binary classification task.
    * @see [[https://en.wikipedia.org/wiki/Precision_and_recall]]
    * */
  object Precision extends Metric {

    override lazy val higherValueIsBetter: Boolean = true

    override def checkInput(y: Target, yPred: Target): Unit = {
      super.checkInput(y, yPred)
      require(numberOfTargetClasses(y) == 2, "Precision is defined for a binary classification task")
    }

    override def calculateValueSafe(y: Target, yPred: Target): Float = {
      val yPredPositive = yPred :== 1.0f
      val yPositive = y :== 1.0f

      val numTp = (yPredPositive &:& yPositive).activeSize.toFloat
      numTp / (numTp + (yPredPositive &:& !yPositive).activeSize.toFloat)
    }

    override def toString: String = "precision"
  }

  /** Recall (sensitivity) - measures the proportion of correctly classified positive examples (true positives)
    * among all <b>actual</b> positive examples.
    *
    * @note Only defined for a binary classification task.
    * @see [[https://en.wikipedia.org/wiki/Precision_and_recall]]
    * */
  object Recall extends Metric {

    override lazy val higherValueIsBetter: Boolean = true

    override def checkInput(y: Target, yPred: Target): Unit = {
      super.checkInput(y, yPred)
      require(numberOfTargetClasses(y) == 2, "Recall is defined for a binary classification task")
    }

    override def calculateValueSafe(y: Target, yPred: Target): Float = {
      val yPredPositive = yPred :== 1.0f
      val yPositive = y :== 1.0f

      val numTp = (yPredPositive &:& yPositive).activeSize.toFloat
      numTp / (numTp + (!yPredPositive &:& yPositive).activeSize.toFloat)
    }

    override def toString: String = "recall"
  }

  /** F1 score - defined as the harmonic average of precision and recall.
    *
    * @note Only defined for a binary classification task.
    * @see [[https://en.wikipedia.org/wiki/F1_score]]
    * */
  object F1Score extends Metric {

    override lazy val higherValueIsBetter: Boolean = true

    override def checkInput(y: Target, yPred: Target): Unit = {
      super.checkInput(y, yPred)
      require(numberOfTargetClasses(y) == 2, "F1 score is defined for a binary classification task")
    }

    override def calculateValueSafe(y: Target, yPred: Target): Float = {
      val prec = precision(y, yPred)
      val rec = recall(y, yPred)

      2.0f * (prec * rec) / (prec + rec)
    }

    override def toString: String = "F1 score"
  }

  /** Hamming loss - measures the proportion of incorrectly classified examples. */
  object HammingLoss extends Metric {

    override lazy val higherValueIsBetter: Boolean = false

    override def calculateValueSafe(y: Target, yPred: Target): Float = 1.0f - accuracy(y, yPred)

    override def toString: String = "Hamming loss"
  }
}
