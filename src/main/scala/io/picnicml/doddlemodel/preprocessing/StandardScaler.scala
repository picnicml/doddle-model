package io.picnicml.doddlemodel.preprocessing

import breeze.linalg.*
import breeze.stats.{mean, stddev}
import io.picnicml.doddlemodel.data.{Features, RealVector}
import io.picnicml.doddlemodel.syntax.OptionSyntax._
import io.picnicml.doddlemodel.typeclasses.Transformer

/** An immutable preprocessor that transforms features by subtracting the mean and scaling to unit variance.
  *
  * Examples:
  * val preprocessor = StandardScaler()
  */
case class StandardScaler private (private val sampleMean: Option[RealVector],
                                   private val sampleStdDev: Option[RealVector])

object StandardScaler {

  def apply(): StandardScaler = StandardScaler(None, None)

  implicit lazy val ev: Transformer[StandardScaler] = new Transformer[StandardScaler] {

    override def isFitted(model: StandardScaler): Boolean =
      model.sampleMean.isDefined && model.sampleStdDev.isDefined

    override def fit(model: StandardScaler, x: Features): StandardScaler = {
      val sampleStdDev = stddev(x(::, *)).t
      sampleStdDev(sampleStdDev :== 0.0) := 1.0
      StandardScaler(Some(mean(x(::, *)).t), Some(sampleStdDev))
    }

    override protected def transformSafe(model: StandardScaler, x: Features): Features =
      (x(*, ::) - model.sampleMean.getOrBreak).apply(*, ::) / model.sampleStdDev.getOrBreak
  }
}
