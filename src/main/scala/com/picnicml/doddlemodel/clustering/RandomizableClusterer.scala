package com.picnicml.doddlemodel.clustering

import java.io.Serializable

import breeze.linalg.DenseVector
import com.picnicml.doddlemodel.base.Clusterer
import com.picnicml.doddlemodel.data.{Features, Target}

import scala.util.Random

trait RandomizableClusterer[A <: RandomizableClusterer[A]] extends Clusterer[A] {
  this: A with Serializable =>

  /** A function that creates an identical clusterer. */
  protected def copy: A

  protected def fit(x: Features, y: Target = DenseVector[Double]())(implicit rand: Random = new Random()): A = {
    require(!this.isFitted, "Called fit on a model that is already trained")
    this.copy.fitSafe(x)
  }

  /**
    * The object is guaranteed not to be fitted.
    */
  protected def fitSafe(x: Features)(implicit rand: Random): A
}
