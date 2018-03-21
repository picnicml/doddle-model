package com.picnicml.doddlemodel

import com.picnicml.doddlemodel.data.RealVector

object Regularization {

  def ridgeLoss(w: RealVector, lambda: Double): Double = .5 * lambda * (w.t * w)

  def ridgeLossGrad(w: RealVector, lambda: Double): RealVector = lambda * w
}
