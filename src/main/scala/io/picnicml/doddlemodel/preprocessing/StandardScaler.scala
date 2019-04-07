package io.picnicml.doddlemodel.preprocessing

import breeze.linalg.*
import breeze.stats.{mean, stddev}
import cats.syntax.option._
import io.picnicml.doddlemodel.data.Feature.FeatureIndex
import io.picnicml.doddlemodel.data.{Features, RealVector}
import io.picnicml.doddlemodel.syntax.OptionSyntax._
import io.picnicml.doddlemodel.typeclasses.Transformer

/** An immutable preprocessor that transforms features by subtracting the mean and scaling to unit variance.
  *
  * @param featureIndex a subset of columns to be transformed by this preprocessor
  *
  * Examples:
  * val preprocessor = StandardScaler()
  * val preprocessorSubsetOfColumns = StandardScaler(FeatureIndex.numerical(List(0, 2, 3)))
  */
case class StandardScaler private (private val sampleMean: Option[RealVector],
                                   private val sampleStdDev: Option[RealVector],
                                   private val featureIndex: Option[FeatureIndex])

object StandardScaler {

  def apply(): StandardScaler = StandardScaler(none, none, none)

  def apply(featureIndex: FeatureIndex): StandardScaler =
    StandardScaler(none, none, featureIndex.some)

  implicit lazy val ev: Transformer[StandardScaler] = new Transformer[StandardScaler] {

    override def isFitted(model: StandardScaler): Boolean =
      model.sampleMean.isDefined && model.sampleStdDev.isDefined

    override def fit(model: StandardScaler, x: Features): StandardScaler = {
      val xToPreprocess = model.featureIndex.fold(x)(index => x(::, index.columnIndices).toDenseMatrix)
      val sampleStdDev = stddev(xToPreprocess(::, *)).t
      sampleStdDev(sampleStdDev :== 0.0) := 1.0
      model.copy(mean(xToPreprocess(::, *)).t.some, sampleStdDev.some)
    }

    override protected def transformSafe(model: StandardScaler, x: Features): Features = model.featureIndex match {
      case Some(index) =>
        this.preprocessSubset(x.copy, index, model.sampleMean.getOrBreak, model.sampleStdDev.getOrBreak)
      case None =>
        (x(*, ::) - model.sampleMean.getOrBreak).apply(*, ::) / model.sampleStdDev.getOrBreak
    }

    private def preprocessSubset(x: Features, index: FeatureIndex, mean: RealVector, stdDev: RealVector): Features = {
      index.columnIndices.zipWithIndex.foreach { case (colIndex, statisticIndex) =>
        (0 until x.rows).foreach { rowIndex =>
          x(rowIndex, colIndex) = (x(rowIndex, colIndex) - mean(statisticIndex)) / stdDev(statisticIndex)
        }
      }
      x
    }
  }
}
