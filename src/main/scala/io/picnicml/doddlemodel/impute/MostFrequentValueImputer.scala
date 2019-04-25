package io.picnicml.doddlemodel.impute

import io.picnicml.doddlemodel.data.Feature.FeatureIndex
import io.picnicml.doddlemodel.data.{Features, RealVector}
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
case class MostFrequentValueImputer private (private val mostFrequent: Option[RealVector],
                                             private val featureIndex: FeatureIndex)

object MostFrequentValueImputer {

  def apply(featureIndex: FeatureIndex): MostFrequentValueImputer =
    MostFrequentValueImputer(None, featureIndex)

  implicit lazy val ev: Transformer[MostFrequentValueImputer] = new Transformer[MostFrequentValueImputer] {

    override def isFitted(model: MostFrequentValueImputer): Boolean = model.mostFrequent.isDefined

    override def fit(model: MostFrequentValueImputer, x: Features): MostFrequentValueImputer = ???

    override protected def transformSafe(model: MostFrequentValueImputer, x: Features): Features = ???
  }
}
