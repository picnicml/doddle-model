package io.picnicml.doddlemodel.typeclasses

import breeze.linalg.DenseVector
import io.picnicml.doddlemodel.data.Features

trait Clusterer[A] extends Estimator[A] {

  def fit(model: A, x: Features): A = {
    require(!isFitted(model), "Called fit on a model that is already fitted")
    fitSafe(copy(model), x)
  }

  def fitPredict(model: A, x: Features): DenseVector[Double] = {
    require(!isFitted(model), "Called fit on a model that is already fitted")
    labelsSafe(fitSafe(copy(model), x))
  }

  /** A function that creates an identical clusterer. */
  protected def copy(model: A): A

  /** A function that is guaranteed to be called on a non-fitted model. **/
  protected def fitSafe(model: A, x: Features): A

  def labels(model: A):  DenseVector[Double] = {
    require(isFitted(model), "Called labels on a model that is not fitted yet")
    labelsSafe(model)
  }

  /** A function that is guaranteed to be called on a fitted model. */
  protected def labelsSafe(model: A): DenseVector[Double]
}
