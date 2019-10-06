package io.picnicml.doddlemodel.dummy.regression

import breeze.linalg.DenseVector
import breeze.stats.{mean => sampleMean}
import cats.syntax.option._
import io.picnicml.doddlemodel.data.{Features, Target}
import io.picnicml.doddlemodel.typeclasses.Regressor

/** An immutable dummy regressor that always predicts the sample mean.
  *
  * Examples:
  * val model = MeanRegressor()
  */
case class MeanRegressor private (mean: Option[Float])

object MeanRegressor {

  def apply(): MeanRegressor = MeanRegressor(none)

  @SerialVersionUID(0L)
  implicit lazy val ev: Regressor[MeanRegressor] = new Regressor[MeanRegressor] {

    override protected def copy(model: MeanRegressor): MeanRegressor = model.copy()

    override def isFitted(model: MeanRegressor): Boolean = model.mean.isDefined

    @inline override protected def targetVariableAppropriate(y: Target): Boolean = true

    override protected def fitSafe(model: MeanRegressor, x: Features, y: Target): MeanRegressor =
      model.copy(mean = sampleMean(y).some)

    override protected def predictSafe(model: MeanRegressor, x: Features): Target =
      DenseVector(Array.fill(x.rows)(model.mean.get))
  }
}
