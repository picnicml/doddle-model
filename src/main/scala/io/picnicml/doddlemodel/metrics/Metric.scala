package io.picnicml.doddlemodel.metrics

import io.picnicml.doddlemodel.data.Target

trait Metric {

  val higherValueIsBetter: Boolean

  def checkInput(y: Target, yPred: Target): Unit = {
    require(y.length == yPred.length, "Target vectors need to be of equal length")
  }

  def calculateValueSafe(y: Target, yPred: Target): Float

  def apply(y: Target, yPred: Target): Float = {
    checkInput(y, yPred)
    calculateValueSafe(y, yPred)
  }
}
