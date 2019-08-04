package io.picnicml.doddlemodel.preprocessing

import breeze.linalg.*
import io.picnicml.doddlemodel.data.Features
import io.picnicml.doddlemodel.preprocessing.Norms.{L2Norm, Norm}
import io.picnicml.doddlemodel.typeclasses.Transformer

case class Normalizer private (private val normFunction: Norm = L2Norm)

object Normalizer {

  implicit lazy val ev: Transformer[Normalizer] = new Transformer[Normalizer] {

    override def isFitted(model: Normalizer): Boolean = true

    override def fit(model: Normalizer, x: Features): Normalizer = model

    override protected def transformSafe(model: Normalizer, x: Features): Features = {
      val rowNorms = model.normFunction(x)
      // no-op for zero vector
      rowNorms(rowNorms :== 0.0) := 1.0
      x(::, *) /:/ rowNorms
    }
  }
}
