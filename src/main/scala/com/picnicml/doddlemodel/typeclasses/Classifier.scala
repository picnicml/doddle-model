package com.picnicml.doddlemodel.typeclasses

import com.picnicml.doddlemodel.data.{Features, Simplex, Target, numberOfTargetClasses}

trait Classifier[A] extends Predictor[A] {

  val numClasses: Option[Int]

  override final def fit(classifier: A, x: Features, y: Target): A = {
    require(!this.isFitted(classifier), "Called fit on a model that is already trained")
    this.fitSafe(this.copy(classifier, numClasses = numberOfTargetClasses(y)), x, y)
  }

  /** A function that creates a new classifier with numClasses set. */
  protected def copy(classifier: A, numClasses: Int): A

  /**
    * A function that is guaranteed to receive an appropriate target variable when called. Additionally,
    * the object is guaranteed not to be fitted.
    */
  protected def fitSafe(classifier: A, x: Features, y: Target): A

  final def predictProba(classifier: A, x: Features): Simplex = {
    require(this.isFitted(classifier), "Called predictProba on a model that is not trained yet")
    this.predictProbaSafe(classifier, x)
  }

  /** A function that is guaranteed to be called on a fitted model. */
  protected def predictProbaSafe(classifier: A, x: Features): Simplex
}
