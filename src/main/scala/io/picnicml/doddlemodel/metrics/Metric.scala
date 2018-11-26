package io.picnicml.doddlemodel.metrics

import io.picnicml.doddlemodel.data.Target

trait Metric {

  val higherValueIsBetter: Boolean

  def apply(y: Target, yPred: Target): Double
}
