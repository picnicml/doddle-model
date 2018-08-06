package com.picnicml.doddlemodel.preprocessing

import breeze.linalg.*
import breeze.stats.{mean, stddev}
import com.picnicml.doddlemodel.typeclasses.Transformer
import com.picnicml.doddlemodel.data.{Features, RealVector}

/** An immutable preprocessor that transforms features by subtracting the mean and scaling to unit variance.
  *
  * Examples:
  * val preprocessor = StandardScaler()
  */
@SerialVersionUID(1L)
class StandardScaler private (val sampleMean: Option[RealVector], val sampleStdDev: Option[RealVector])
  extends Transformer[StandardScaler] with Serializable {

  override def isFitted: Boolean = this.sampleMean.isDefined && this.sampleStdDev.isDefined

  override def fit(x: Features): StandardScaler = {
    val sampleStdDev = stddev(x(::, *)).t
    sampleStdDev(sampleStdDev :== 0.0) := 1.0
    new StandardScaler(Some(mean(x(::, *)).t), Some(sampleStdDev))
  }

  override protected def transformSafe(x: Features): Features =
    (x(*, ::) - this.sampleMean.get).apply(*, ::) / this.sampleStdDev.get
}

object StandardScaler {

  def apply(): StandardScaler = new StandardScaler(None, None)
}
