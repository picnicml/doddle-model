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
  * @param featureIndex a subset of columns to be imputed by this preprocessor
  *
  * Examples:
  * val imputer = MeanValueImputer()
  * val imputerSubsetOfColumns = MeanValueImputer(FeatureIndex.numerical(List(0, 2, 3)))
  */
case class MeanValueImputer private (private[impute] val means: Option[RealVector],
                                     private val featureIndex: Option[FeatureIndex])

object MeanValueImputer {

  def apply(): MeanValueImputer = MeanValueImputer(none, none)

  def apply(featureIndex: FeatureIndex): MeanValueImputer = MeanValueImputer(none, featureIndex.some)

  implicit val ev: Transformer[MeanValueImputer] = new Transformer[MeanValueImputer] {

    override def isFitted(model: MeanValueImputer): Boolean = model.means.isDefined

    override def fit(model: MeanValueImputer, x: Features): MeanValueImputer = {
      val xToPreprocess = model.featureIndex.fold(x)(index => x(::, index.columnIndices).toDenseMatrix)
      val means = DenseVector.zeros[Double](xToPreprocess.cols)
      0 until xToPreprocess.cols foreach { colIndex =>
        means(colIndex) = mean(xToPreprocess(xToPreprocess(::, colIndex).findAll(!_.isNaN), colIndex))
      }
      model.copy(means.some)
    }

    override protected def transformSafe(model: MeanValueImputer, x: Features): Features = {
      val xCopy = x.copy
      val colIndices = model.featureIndex.fold[IndexedSeq[Int]](0 until xCopy.cols)(index => index.columnIndices)
      colIndices.zipWithIndex.foreach { case (colIndex, statisticIndex) =>
        xCopy(::, colIndex).findAll(_.isNaN).iterator.foreach { rowIndex =>
          xCopy(rowIndex, colIndex) = model.means.getOrBreak(statisticIndex)
        }
      }
      xCopy
    }
  }
}
