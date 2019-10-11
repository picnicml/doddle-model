package io.picnicml.doddlemodel.preprocessing

import breeze.linalg.{*, convert}
import breeze.stats.{mean, stddev}
import cats.syntax.option._
import io.picnicml.doddlemodel.data.Feature.FeatureIndex
import io.picnicml.doddlemodel.data.{Features, RealVector}
import io.picnicml.doddlemodel.syntax.OptionSyntax._
import io.picnicml.doddlemodel.typeclasses.Transformer

/** An immutable preprocessor that transforms features by subtracting the mean and scaling to unit variance.
  *
  * @param featureIndex feature index associated with features, this is needed so that only numerical features
  *                     are transformed by this preprocessor, could be a subset of columns to be transformed
  *
  * Examples:
  * val preprocessor = StandardScaler(featureIndex)
  * val preprocessorSubsetOfColumns = StandardScaler(featureIndex.subset("f0", "f2"))
  */
case class StandardScaler private (private val sampleMean: Option[RealVector],
                                   private val sampleStdDev: Option[RealVector],
                                   private val featureIndex: FeatureIndex)

object StandardScaler {

  def apply(featureIndex: FeatureIndex): StandardScaler = StandardScaler(none, none, featureIndex)

  @SerialVersionUID(0L)
  implicit lazy val ev: Transformer[StandardScaler] = new Transformer[StandardScaler] {

    override def isFitted(model: StandardScaler): Boolean =
      model.sampleMean.isDefined && model.sampleStdDev.isDefined

    override def fit(model: StandardScaler, x: Features): StandardScaler = {
      val xToPreprocess = x(::, model.featureIndex.numerical.columnIndices)
      val sampleStdDev = convert(stddev(xToPreprocess(::, *)).t.toDenseVector, Float)
      sampleStdDev(sampleStdDev :== 0.0f) := 1.0f
      model.copy(mean(xToPreprocess(::, *)).t.toDenseVector.some, sampleStdDev.some)
    }

    override protected def transformSafe(model: StandardScaler, x: Features): Features = {
      val xCopy = x.copy
      model.featureIndex.numerical.columnIndices.zipWithIndex.foreach { case (colIndex, statisticIndex) =>
        (0 until xCopy.rows).foreach { rowIndex =>
          xCopy(rowIndex, colIndex) = (xCopy(rowIndex, colIndex) - model.sampleMean.getOrBreak(statisticIndex)) /
            model.sampleStdDev.getOrBreak(statisticIndex)
        }
      }
      xCopy
    }
  }
}
