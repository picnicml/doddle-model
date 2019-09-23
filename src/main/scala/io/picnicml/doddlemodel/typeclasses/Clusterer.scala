package io.picnicml.doddlemodel.typeclasses

import io.picnicml.doddlemodel.data.Features

trait Clusterer[A] extends Estimator[A] {

  def fit(model: A, x: Features): A = {
    require(!isFitted(model), "Called fit on a model that is already fitted")
    fitSafe(copy(model), x)
  }

  def fitPredict(model: A, x: Features): Array[Int] = {
    require(!isFitted(model), "Called fit on a model that is already fitted")
    labelSafe(fitSafe(copy(model), x))
  }

  def labels(model: A): Array[Int] = {
    require(isFitted(model), "Request labels on a model that is not fitted yet")
    labelSafe(model)
  }

  /** A function that creates an identical clusterer. */
  protected def copy(model: A): A

  /** A function that is guaranteed to be called on a fitted model. */
  protected def labelSafe(model: A): Array[Int]

  /**
    * A function that is guaranteed to receive an appropriate target variable when called. Additionally,
    * the object is guaranteed not to be fitted.
    */
  protected def fitSafe(model: A, x: Features): A
}
