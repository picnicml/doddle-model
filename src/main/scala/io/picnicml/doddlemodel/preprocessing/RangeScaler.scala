package io.picnicml.doddlemodel.preprocessing

import breeze.linalg.{*, Axis, max, min}
import cats.syntax.option._
import io.picnicml.doddlemodel.data.Feature.FeatureIndex
import io.picnicml.doddlemodel.data.{Features, RealVector}
import io.picnicml.doddlemodel.typeclasses.Transformer
import io.picnicml.doddlemodel.syntax.OptionSyntax._

case class RangeScaler private (private val scale: Option[RealVector],
                                private val minAdjustment: Option[RealVector],
                                private val range: (Double, Double),
                                private val featureIndex: FeatureIndex)

object RangeScaler {

  def apply(range: (Double, Double), featureIndex: FeatureIndex): RangeScaler = {
    val (lowerBound, upperBound) = range
    val numNumeric = featureIndex.numerical.columnIndices.length
    require(numNumeric > 0, "There must be at least 1 numeric column in the given data")
    require(upperBound > lowerBound, "Upper bound of range must be greater than lower bound")
    RangeScaler(none, none, range, featureIndex)
  }

  implicit lazy val ev: Transformer[RangeScaler] = new Transformer[RangeScaler] {

    override def fit(model: RangeScaler, x: Features): RangeScaler = {
      val (lowerBound, upperBound) = model.range
      val numericColsOnly = x(::, model.featureIndex.numerical.columnIndices).toDenseMatrix
      val (colMax: RealVector, colMin: RealVector) =
        (max(numericColsOnly, Axis._0).inner, min(numericColsOnly, Axis._0).inner)
      val dataRange = colMax - colMin
      // avoid division by zero for constant features (max == min)
      dataRange(dataRange :== 0.0) := 1.0

      val scale = (upperBound - lowerBound) / dataRange
      val minAdjustment = lowerBound - (colMin *:* scale)

      model.copy(scale.some, minAdjustment.some)
    }

    override protected def transformSafe(model: RangeScaler, x: Features): Features = {
      val numericColsOnly = x(::, model.featureIndex.numerical.columnIndices).toDenseMatrix
      val colsScaled: Features = numericColsOnly(*, ::) *:* model.scale.getOrBreak
      colsScaled(*, ::) +:+ model.minAdjustment.getOrBreak
    }

    override def isFitted(model: RangeScaler): Boolean =
      model.scale.isDefined && model.minAdjustment.isDefined
  }

}
