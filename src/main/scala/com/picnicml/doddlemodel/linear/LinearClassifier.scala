package com.picnicml.doddlemodel.linear

import java.io.Serializable

import breeze.linalg.DenseVector
import com.picnicml.doddlemodel.base.Classifier
import com.picnicml.doddlemodel.data.{Features, RealVector, Simplex, Target}

trait LinearClassifier[A <: LinearClassifier[A]] extends Classifier[A] with LinearModel[A] {
  this: Serializable =>

  /** A stateless function that predicts probability of each class. */
  protected def predictProba(w: RealVector, x: Features): Simplex

  override protected def fitSafe(x: Features, y: Target): A = {
    val numClasses = this.numClasses match {
      case Some(nc) => nc
      case None => throw new IllegalStateException("numClasses must be set prior to calling fitSafe()")
    }

    val wLength = (x.cols + 1) * (numClasses - 1)
    this.copy(w = this.maximumLikelihood(this.xWithBiasTerm(x), y, DenseVector.zeros[Double](wLength)))
  }

  override protected def predictProbaSafe(x: Features): Simplex = {
    require(this.numClasses.isDefined)
    this.predictProba(this.w.get, this.xWithBiasTerm(x))
  }
}
