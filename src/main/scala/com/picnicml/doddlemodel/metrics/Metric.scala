package com.picnicml.doddlemodel.metrics

import com.picnicml.doddlemodel.data.Target

trait Metric {

  val higherValueIsBetter: Boolean

  def apply(y: Target, yPred: Target): Double
}
