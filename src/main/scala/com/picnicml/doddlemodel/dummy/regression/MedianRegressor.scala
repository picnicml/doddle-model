package com.picnicml.doddlemodel.dummy.regression

import breeze.linalg.DenseVector
import breeze.stats.{median => sampleMedian}
import com.picnicml.doddlemodel.data.{Features, Target}
import com.picnicml.doddlemodel.typeclasses.Regressor

/** An immutable dummy regressor that always predicts the sample median.
  *
  * Examples:
  * val model = MedianRegressor()
  */
case class MedianRegressor private (median: Option[Double])

object MedianRegressor {

  def apply(): MedianRegressor = new MedianRegressor(None)

  implicit lazy val ev: Regressor[MedianRegressor] = new Regressor[MedianRegressor] {

    override protected def copy(model: MedianRegressor): MedianRegressor = model.copy()

    override def isFitted(model: MedianRegressor): Boolean = model.median.isDefined

    @inline override protected def targetVariableAppropriate(y: Target): Boolean = true

    override protected def fitSafe(model: MedianRegressor, x: Features, y: Target): MedianRegressor =
      model.copy(median = Some(sampleMedian(y)))

    override protected def predictSafe(model: MedianRegressor, x: Features): Target =
      DenseVector(Array.fill(x.rows)(model.median.get))
  }
}
