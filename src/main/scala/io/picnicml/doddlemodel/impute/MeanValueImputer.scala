package io.picnicml.doddlemodel.impute

import breeze.linalg.DenseVector
import breeze.stats.mean
import io.picnicml.doddlemodel.data.{Features, RealVector}
import io.picnicml.doddlemodel.syntax.OptionSyntax._
import io.picnicml.doddlemodel.typeclasses.Transformer

/** An immutable simple imputer that replaces all NaN values with column means.
  *
  * Examples:
  * val imputer = MeanValueImputer()
  */
case class MeanValueImputer private (private[impute] val means: Option[RealVector])

object MeanValueImputer {

  def apply(): MeanValueImputer = MeanValueImputer(None)

  implicit val ev: Transformer[MeanValueImputer] = new Transformer[MeanValueImputer] {

    override def isFitted(model: MeanValueImputer): Boolean = model.means.isDefined

    override def fit(model: MeanValueImputer, x: Features): MeanValueImputer = {
      val means = DenseVector.zeros[Double](x.cols)
      0 until x.cols foreach { colIndex =>
        means(colIndex) = mean(x(x(::, colIndex).findAll(!_.isNaN), colIndex))
      }
      model.copy(Some(means))
    }

    override protected def transformSafe(model: MeanValueImputer, x: Features): Features = {
      val xCopy = x.copy
      0 until xCopy.cols foreach { colIndex =>
        xCopy(::, colIndex).findAll(_.isNaN).iterator.foreach { rowIndex =>
          xCopy(rowIndex, colIndex) = model.means.getOrBreak(colIndex)
        }
      }
      xCopy
    }
  }
}
