package com.picnicml.doddlemodel.linear.typeclasses

import breeze.linalg.DenseVector
import com.picnicml.doddlemodel.data.{Features, RealVector, Simplex, Target}
import com.picnicml.doddlemodel.typeclasses.Classifier

trait LinearClassifierNew[A] extends LinearModelNew[A] with Classifier[A] {

  /** A stateless function that predicts probability of each class. */
  protected def predictProba(w: RealVector, x: Features): Simplex

  override protected def fitSafe(linearClassifier: A, x: Features, y: Target): A = {
    val wLength = (x.cols + 1) * (numClasses(linearClassifier).get - 1)
    val wInitial = DenseVector.zeros[Double](wLength)
    copy(linearClassifier, w = maximumLikelihood(linearClassifier, xWithBiasTerm(x), y, wInitial))
  }

  override protected def predictProbaSafe(linearClassifier: A, x: Features): Simplex =
    predictProba(w(linearClassifier).get, xWithBiasTerm(x))
}
