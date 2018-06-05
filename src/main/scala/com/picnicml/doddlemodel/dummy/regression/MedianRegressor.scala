package com.picnicml.doddlemodel.dummy.regression

import breeze.linalg.DenseVector
import breeze.stats.{median => sampleMedian}
import com.picnicml.doddlemodel.base.Regressor
import com.picnicml.doddlemodel.data.{Features, Target}

/** An immutable dummy regressor that always predicts the sample median.
  *
  * Examples:
  * val model = MedianRegressor()
  */
@SerialVersionUID(1L)
class MedianRegressor private (val median: Option[Double]) extends Regressor[MedianRegressor] with Serializable {

  override protected def copy: MedianRegressor = this

  override def isFitted: Boolean = this.median.isDefined

  override protected def targetVariableAppropriate(y: Target): Boolean = true

  override protected def fitSafe(x: Features, y: Target): MedianRegressor =
    new MedianRegressor(Some(sampleMedian(y)))

  override protected def predictSafe(x: Features): Target =
    DenseVector(Array.fill(x.rows)(median.get))
}

object MedianRegressor {

  def apply(): MedianRegressor = new MedianRegressor(None)
}
