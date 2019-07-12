package io.picnicml.doddlemodel.preprocessing

import breeze.linalg.{*, Axis, max, sum}
import breeze.numerics.{abs, pow, sqrt}
import io.picnicml.doddlemodel.data.{Features, RealVector}
import io.picnicml.doddlemodel.typeclasses.Transformer

case class Normalizer private (private val normFunction: Features => RealVector)

object Normalizer {

  def apply(norm: String = "l2"): Normalizer = {
    // TODO: expose norms for re-use
    val normFunction = norm match {
      case "l2" => (x: Features) => sqrt(sum(pow(x, 2), Axis._1))
      case "l1" => (x: Features) => sum(abs(x), Axis._1)
      case "max" => (x: Features) => max(abs(x), Axis._1)
      case _ => throw new IllegalArgumentException("Unsupported norm")
    }
    new Normalizer(normFunction)
  }

  implicit lazy val ev: Transformer[Normalizer] = new Transformer[Normalizer] {
    override def fit(model: Normalizer, x: Features): Normalizer = model

    override protected def transformSafe(model: Normalizer, x: Features): Features = {
      val rowNorms = model.normFunction(x)
      // no-op for zero vector
      rowNorms(rowNorms :== 0.0) := 1.0
      x(::, *) /:/ rowNorms
    }

    override def isFitted(model: Normalizer): Boolean = true
  }
}
