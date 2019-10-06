package io.picnicml.doddlemodel.preprocessing

import breeze.linalg.{Axis, max, min}
import cats.syntax.option._
import io.picnicml.doddlemodel.data.Feature.FeatureIndex
import io.picnicml.doddlemodel.data.{Features, RealVector}
import io.picnicml.doddlemodel.syntax.OptionSyntax._
import io.picnicml.doddlemodel.typeclasses.Transformer

case class RangeScaler private (private val scale: Option[RealVector],
                                private val minAdjustment: Option[RealVector],
                                private val range: (Float, Float),
                                private val featureIndex: FeatureIndex)

/** An immutable preprocessor that scales numerical features to a specified range.
  * Non-numerical features are left untouched.
  * */
object RangeScaler {

  /** Create a RangeScaler to scale numerical features to the range [0, 1] (i.e. both bounds included).
    *
    * @param range lower and upper bound of range
    * @param featureIndex feature index associated with features - this is needed so that only numerical features are
    *                     transformed by this preprocessor; could be a subset of columns to be transformed
    *
    * @example Scale a matrix with two features (one numerical and one categorical) to range [0.0, 1.0].
    *   {{{
    *     import io.picnicml.doddlemodel.preprocessing.RangeScaler.ev
    *
    *     val featureIndex = FeatureIndex(List(NumericalFeature, CategoricalFeature))
    *     val x = DenseMatrix(
    *       List(2.0f, 1.0f),
    *       List(3.0f, 0.0f),
    *       List(0.0f, 0.0f),
    *       List(5.0f, 1.0f)
    *     )
    *     val rangeScaler = RangeScaler((0.0f, 1.0f), featureIndex)
    *     val trainedRangeScaler = ev.fit(rangeScaler, x)
    *     ev.transform(trainedRangeScaler, x)
    *   }}}
    */
  def apply(range: (Float, Float), featureIndex: FeatureIndex): RangeScaler = {
    val (lowerBound, upperBound) = range
    require(upperBound > lowerBound, "Upper bound of range must be greater than lower bound")
    RangeScaler(none, none, range, featureIndex)
  }

  @SerialVersionUID(0L)
  implicit lazy val ev: Transformer[RangeScaler] = new Transformer[RangeScaler] {

    override def isFitted(model: RangeScaler): Boolean =
      model.scale.isDefined && model.minAdjustment.isDefined

    override def fit(model: RangeScaler, x: Features): RangeScaler = {
      val (lowerBound, upperBound) = model.range
      val numericColIndices = model.featureIndex.numerical.columnIndices
      val colMax = max(x(::, numericColIndices), Axis._0).t.toDenseVector
      val colMin = min(x(::, numericColIndices), Axis._0).t.toDenseVector
      val dataRange = colMax - colMin
      // avoid division by zero for constant features (max == min)
      dataRange(dataRange :== 0.0f) := 1.0f

      val scale = (upperBound - lowerBound) / dataRange
      val minAdjustment = lowerBound - (colMin *:* scale)

      model.copy(scale.some, minAdjustment.some)
    }

    override protected def transformSafe(model: RangeScaler, x: Features): Features = {
      val xCopy = x.copy
      val scale = model.scale.getOrBreak
      val minAdjustment = model.minAdjustment.getOrBreak
      model.featureIndex.numerical.columnIndices.zipWithIndex.foreach {
        case (colIndex, idx) =>
          xCopy(::, colIndex) := (xCopy(::, colIndex) *:* scale(idx)) +:+ minAdjustment(idx)
      }

      xCopy
    }
  }
}
