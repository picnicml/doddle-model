package com.picnicml.doddlemodel.metrics

import breeze.linalg.sum
import com.picnicml.doddlemodel.data.Types.Target

object ClassificationMetrics {

  /** Classification accuracy. */
  def accuracy(y: Target, yPred: Target): Double = {
    val correct = (y :== yPred).map(x => if (x) 1 else 0)
    sum(correct) / correct.length.toDouble
  }
}
