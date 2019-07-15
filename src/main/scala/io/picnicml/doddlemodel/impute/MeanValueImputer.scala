package io.picnicml.doddlemodel.impute

import breeze.linalg.DenseVector
import breeze.stats.mean
import cats.syntax.option._
import io.picnicml.doddlemodel.data.Feature.FeatureIndex
import io.picnicml.doddlemodel.data.{Features, RealVector}
import io.picnicml.doddlemodel.syntax.OptionSyntax._
import io.picnicml.doddlemodel.typeclasses.Transformer

case class MeanValueImputer private (private[impute] val means: Option[RealVector],
                                     private val featureIndex: FeatureIndex)

/** An immutable simple imputer that replaces all NaN values with column means. */
object MeanValueImputer {

  /** Create an imputer based on a feature index.
    *
    * @param featureIndex feature index associated with features, this is needed so that only numerical features
    *                     are transformed by this preprocessor, could be a subset of columns to be transformed
    *
    * @example Impute values for all (numerical) features.
    *   {{{
    *     val featureIndex = FeatureIndex(List(NumericalFeature, CategoricalFeature, NumericalFeature,
    *       NumericalFeature))
    *     val imputer = MeanValueImputer(featureIndex)
    *   }}}
    *
    * @example Impute values for a subset of features.
    *   {{{
    *     val featureIndex = FeatureIndex(List("f0", "f1", "f2"), List(NumericalFeature, NumericalFeature,
    *       NumericalFeature), List(0, 1, 2))
    *     val imputerSubsetOfColumns = MeanValueImputer(featureIndex.subset("f0", "f2"))
    *   }}}
    */
  def apply(featureIndex: FeatureIndex): MeanValueImputer = MeanValueImputer(none, featureIndex)

  @SerialVersionUID(0L)
  implicit val ev: Transformer[MeanValueImputer] = new Transformer[MeanValueImputer] {

    override def isFitted(model: MeanValueImputer): Boolean = model.means.isDefined

    override def fit(model: MeanValueImputer, x: Features): MeanValueImputer = {
      val xToPreprocess = x(::, model.featureIndex.numerical.columnIndices)
      val means = DenseVector.zeros[Float](xToPreprocess.cols)
      0 until xToPreprocess.cols foreach { colIndex =>
        means(colIndex) = mean(xToPreprocess(xToPreprocess(::, colIndex).findAll(!_.isNaN), colIndex))
      }
      model.copy(means.some)
    }

    override protected def transformSafe(model: MeanValueImputer, x: Features): Features = {
      val xCopy = x.copy
      model.featureIndex.numerical.columnIndices.zipWithIndex.foreach { case (colIndex, statisticIndex) =>
        xCopy(::, colIndex).findAll(_.isNaN).iterator.foreach { rowIndex =>
          xCopy(rowIndex, colIndex) = model.means.getOrBreak(statisticIndex)
        }
      }
      xCopy
    }
  }
}
