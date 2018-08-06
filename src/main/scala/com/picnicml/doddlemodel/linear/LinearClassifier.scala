package com.picnicml.doddlemodel.linear

import breeze.linalg.DenseVector
import com.picnicml.doddlemodel.data.{Features, RealVector, Simplex, Target}
import com.picnicml.doddlemodel.typeclasses.Classifier

trait LinearClassifier[A] extends Classifier[A] with LinearModel[A] {

  /** A stateless function that predicts probability of each class. */
  protected def predictProba(classifier: A, w: RealVector, x: Features): Simplex

  final override protected def fitSafe(classifier: A, x: Features, y: Target): A = {
    val wLength = (x.cols + 1) * (this.numClasses(classifier).get - 1)
    val w = this.maximumLikelihood(classifier, this.xWithBiasTerm(x), y, DenseVector.zeros[Double](wLength))
    this.copy(classifier, w)
  }

  final override protected def predictProbaSafe(classifier: A, x: Features): Simplex =
    this.predictProba(classifier, this.w(classifier).get, this.xWithBiasTerm(x))
}
