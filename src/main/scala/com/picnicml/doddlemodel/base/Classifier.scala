package com.picnicml.doddlemodel.base

import java.io.Serializable

import breeze.linalg.unique
import com.picnicml.doddlemodel.data.{Features, Simplex, Target}

abstract class Classifier[A <: Classifier[A]] extends Predictor[A] {
  this: Serializable =>

  val numClasses: Option[Int]

  override def fit(x: Features, y: Target): A = {
    require(!this.isFitted, "Called fit on a model that is already trained")

    val targetClasses = unique(y)
    require(targetClasses.length >= 2,
      "Target variable must be comprised of at least two categories")
    require(targetClasses.toArray.sorted sameElements Array.range(0, targetClasses.length),
      "Invalid encoding of categories in the target variable")

    this.copy(numClasses = targetClasses.length).fitSafe(x, y)
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
