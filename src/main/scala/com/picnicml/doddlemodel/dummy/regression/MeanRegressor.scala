package com.picnicml.doddlemodel.dummy.regression

import breeze.linalg.DenseVector
import breeze.stats.{mean => sampleMean}
import com.picnicml.doddlemodel.typeclasses.Regressor
import com.picnicml.doddlemodel.data.{Features, Target}

/** An immutable dummy regressor that always predicts the sample mean.
  *
  * Examples:
  * val model = MeanRegressor()
  */
@SerialVersionUID(1L)
class MeanRegressor private (val mean: Option[Double]) extends Regressor[MeanRegressor] with Serializable {

  override protected def copy: MeanRegressor = this

  override def isFitted: Boolean = this.mean.isDefined

  override protected def targetVariableAppropriate(y: Target): Boolean = true

  override protected def fitSafe(x: Features, y: Target): MeanRegressor =
    new MeanRegressor(Some(sampleMean(y)))

  override protected def predictSafe(x: Features): Target =
    DenseVector(Array.fill(x.rows)(mean.get))
}

object MeanRegressor {

  def apply(): MeanRegressor = new MeanRegressor(None)
}
