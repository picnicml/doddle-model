package io.picnicml.doddlemodel.impute

import breeze.linalg.DenseVector
import breeze.stats.mean
import cats.syntax.option._
import io.picnicml.doddlemodel.data.Feature.FeatureIndex
import io.picnicml.doddlemodel.data.{Features, RealVector}
import io.picnicml.doddlemodel.syntax.OptionSyntax._
import io.picnicml.doddlemodel.typeclasses.Transformer

/** An immutable simple imputer that replaces all NaN values with column means.
  *
  * @param featureIndex feature index associated with features, this is needed so that only numerical features
  *                     are transformed by this preprocessor, could be a subset of columns to be transformed
  *
  * Examples:
  * val imputer = MeanValueImputer(featureIndex)
  * val imputerSubsetOfColumns = MeanValueImputer(featureIndex.subset("f0", "f2"))
  */
case class MeanValueImputer private (private[impute] val means: Option[RealVector],
                                     private val featureIndex: FeatureIndex)

object MeanValueImputer {

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
