package io.picnicml.doddlemodel.impute

import breeze.linalg.{DenseVector, SliceVector}
import cats.syntax.option._
import io.picnicml.doddlemodel.data.Feature.FeatureIndex
import io.picnicml.doddlemodel.data.{Features, RealVector}
import io.picnicml.doddlemodel.syntax.OptionSyntax._
import io.picnicml.doddlemodel.typeclasses.Transformer

/** An immutable simple imputer that replaces all NaN values with most frequent value of a corresponding column.
  *
  * @param featureIndex feature index associated with features, this is needed so that only categorical features
  *                     are transformed by this preprocessor, could be a subset of columns to be transformed
  *
  * Examples:
  * val imputer = MostFrequentValueImputer(featureIndex)
  * val imputerSubsetOfColumns = MostFrequentValueImputer(featureIndex.subset("f0", "f2"))
  */
case class MostFrequentValueImputer private (private[impute] val mostFrequent: Option[RealVector],
                                             private val featureIndex: FeatureIndex)

object MostFrequentValueImputer {

  def apply(featureIndex: FeatureIndex): MostFrequentValueImputer =
    MostFrequentValueImputer(None, featureIndex)

  @SerialVersionUID(0L)
  implicit lazy val ev: Transformer[MostFrequentValueImputer] = new Transformer[MostFrequentValueImputer] {

    override def isFitted(model: MostFrequentValueImputer): Boolean = model.mostFrequent.isDefined

    override def fit(model: MostFrequentValueImputer, x: Features): MostFrequentValueImputer = {
      val xToPreprocess = x(::, model.featureIndex.categorical.columnIndices)
      val mostFrequent = DenseVector.zeros[Float](xToPreprocess.cols)
      0 until xToPreprocess.cols foreach { colIndex =>
        mostFrequent(colIndex) = getMostFrequent(xToPreprocess(xToPreprocess(::, colIndex).findAll(!_.isNaN), colIndex))
      }
      model.copy(mostFrequent.some)
    }

    private def getMostFrequent(column: SliceVector[(Int, Int), Float]): Float = {
      val counts = scala.collection.mutable.Map.empty[Float, Int].withDefaultValue(0)
      column.foreachValue(value => counts(value) = counts(value) + 1)
      counts.maxBy(_._2)._1
    }

    override protected def transformSafe(model: MostFrequentValueImputer, x: Features): Features = {
      val xCopy = x.copy
      model.featureIndex.categorical.columnIndices.zipWithIndex.foreach { case (colIndex, statisticIndex) =>
        xCopy(::, colIndex).findAll(_.isNaN).iterator.foreach { rowIndex =>
          xCopy(rowIndex, colIndex) = model.mostFrequent.getOrBreak(statisticIndex)
        }
      }
      xCopy
    }
  }
}
