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

/** An immutable simple imputer that replaces numerical NaN values with column means. Categorical values are left
  * untouched. */
object MeanValueImputer {

  /** Create an imputer based on a feature index.
    *
    * @param featureIndex feature index associated with features - this is needed so that only numerical features
    *                     are transformed by this preprocessor, could be a subset of columns to be transformed
    *
    * @example Impute values for all (numerical) features.
    *   {{{
    *     import io.picnicml.doddlemodel.data.CsvLoader.loadCsvDataset
    *     import io.picnicml.doddlemodel.impute.MeanValueImputer
    *     import io.picnicml.doddlemodel.syntax.TransformerSyntax._
    *
    *     val (data, featureInfo) = loadCsvDataset("src/main/resources/datasets/dummy_csv_reading.csv", "NA")
    *     val imputer = MeanValueImputer(featureInfo)
    *     val fittedImputer = imputer.fit(data)
    *     // Note: only fourth (index 3) column gets imputed as it's the only numerical column with NAs
    *     fittedImputer.transform(data)
    *   }}}
    *
    * @example Impute values for a subset of features.
    *   {{{
    *     import io.picnicml.doddlemodel.data.CsvLoader.loadCsvDataset
    *     import io.picnicml.doddlemodel.impute.MeanValueImputer
    *     import io.picnicml.doddlemodel.syntax.TransformerSyntax._
    *
    *     val (data, featureInfo) = loadCsvDataset("src/main/resources/datasets/dummy_csv_reading.csv", "NA")
    *     val imputerSubset = MeanValueImputer(featureInfo.subset("f3"))
    *     val fittedImputer = imputerSubset.fit(data)
    *     fittedImputer.transform(data)
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
