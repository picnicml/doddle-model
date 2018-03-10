package com.doddlemodel.linear

import com.doddlemodel.base.Classifier
import com.doddlemodel.data.Types.{Features, RealVector, Simplex, Target}

trait LinearClassifier {
  this: LinearModel with Classifier =>

  /** A function that creates a new classifier with model parameters w. */
  protected def copy(w: RealVector): Classifier

  /** A stateless function that predicts probability for each class. */
  protected def predictProba(w: RealVector, x: Features): Simplex

  override def fit(x: Features, y: Target): Classifier = {
    require(!this.isTrained, "Called fit on a model that is already trained")
    this.copy(this.findModelParameters(x, y))
  }

  override def predictProba(x: Features): Simplex = {
    require(this.isTrained, "Called predictProba on a model that is not trained yet")
    this.predictProba(this.w.get, this.xWithBiasTerm(x))
  }
}
