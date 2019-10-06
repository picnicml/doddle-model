package io.picnicml.doddlemodel.linear.typeclasses

import breeze.linalg.DenseVector
import io.picnicml.doddlemodel.data.{Features, RealVector, Simplex, Target}
import io.picnicml.doddlemodel.syntax.OptionSyntax._
import io.picnicml.doddlemodel.typeclasses.Classifier

trait LinearClassifier[A] extends LinearModel[A] with Classifier[A] {

  /** A stateless function that predicts probability of each class. */
  protected def predictProbaStateless(model: A, w: RealVector, x: Features): Simplex

  override protected def fitSafe(model: A, x: Features, y: Target): A = {
    val wLength = (x.cols + 1) * (numClasses(model).getOrBreak - 1)
    val wInitial = DenseVector.zeros[Float](wLength)
    copy(model, w = maximumLikelihood(model, xWithBiasTerm(x), y, wInitial))
  }

  override protected def predictProbaSafe(model: A, x: Features): Simplex =
    predictProbaStateless(model, w(model).get, xWithBiasTerm(x))
}
