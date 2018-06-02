package com.picnicml.doddlemodel.preprocessing

import breeze.linalg.*
import breeze.stats.{mean, stddev}
import com.picnicml.doddlemodel.base.Transformer
import com.picnicml.doddlemodel.data.{Dataset, Features, RealVector, Target}

/** An immutable preprocessor that transforms features by subtracting the mean and scaling to unit variance.
  *
  * Examples:
  * val preprocessor = StandardScaler()
  */
@SerialVersionUID(1L)
class StandardScaler private (val meanStdDev: Option[MeanStdDev])
  extends Transformer[StandardScaler] with Serializable {

  override def isFitted: Boolean = this.meanStdDev.isDefined

  override def fit(x: Features, y: Target): StandardScaler =
    new StandardScaler(Some(MeanStdDev(mean(x(::, *)).t, stddev(x(::, *)).t)))

  override protected def transformSafe(x: Features, y: Target): Dataset =
    ((x(*, ::) - meanStdDev.get.mean).apply(*, ::) / meanStdDev.get.stdDev, y)
}

object StandardScaler {

  def apply(): StandardScaler = new StandardScaler(None)
}

case class MeanStdDev(mean: RealVector, stdDev: RealVector)
