package com.picnicml.doddlemodel.base

import java.io.Serializable

import com.picnicml.doddlemodel.data.{Features, Simplex, Target, numberOfTargetClasses}

abstract class Classifier[A <: Classifier[A]] extends Predictor[A] {
  this: A with Serializable =>

  val numClasses: Option[Int]

  override def fit(x: Features, y: Target): A = {
    require(!this.isFitted, "Called fit on a model that is already trained")
    this.copy(numClasses = numberOfTargetClasses(y)).fitSafe(x, y)
  }

  /** A function that creates a new classifier with numClasses set. */
  protected def copy(numClasses: Int): A

  /**
    * A function that is guaranteed to receive an appropriate target variable when called. Additionally,
    * the object is guaranteed not to be fitted.
    */
  protected def fitSafe(x: Features, y: Target): A

  def predictProba(x: Features): Simplex = {
    require(this.isFitted, "Called predictProba on a model that is not trained yet")
    this.predictProbaSafe(x)
  }

  /** A function that is guaranteed to be called on a fitted model. */
  protected def predictProbaSafe(x: Features): Simplex
}
