package com.picnicml.doddlemodel.linear

import breeze.linalg.{DenseVector, unique}
import com.picnicml.doddlemodel.base.Classifier
import com.picnicml.doddlemodel.data.{Features, RealVector, Simplex, Target}

trait LinearClassifier extends Classifier with LinearModel {

  /** A function that creates a new linear classifier with numClasses set. */
  protected def copy(numClasses: Int): LinearClassifier

  /** A function that creates a new classifier with model parameters w. */
  protected def copy(w: RealVector): Classifier

  /** A stateless function that predicts probability for each class. */
  protected def predictProba(w: RealVector, x: Features): Simplex

  override def fit(x: Features, y: Target): Classifier = {
    require(!this.isFitted, "Called fit on a model that is already trained")
    require(this.numClasses.isEmpty)

    val targetClasses = unique(y)
    require(targetClasses.length >= 2,
      "Target variable must be comprised of at least two categories")
    require(targetClasses.data.sorted sameElements Array.range(0, targetClasses.length),
      "Invalid encoding of categories in the target variable")

    val trainedModel = this.copy(numClasses = targetClasses.length)
    val wLength = (x.cols + 1) * (targetClasses.length - 1)
    val w = trainedModel.maximumLikelihood(trainedModel.xWithBiasTerm(x), y, DenseVector.zeros[Double](wLength))
    trainedModel.copy(w)
  }

  override def predictProba(x: Features): Simplex = {
    require(this.isFitted, "Called predictProba on a model that is not trained yet")
    require(this.numClasses.isDefined)

    this.predictProba(this.w.get, this.xWithBiasTerm(x))
  }
}
