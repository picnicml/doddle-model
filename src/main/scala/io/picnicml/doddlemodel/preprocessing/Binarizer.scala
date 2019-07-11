package io.picnicml.doddlemodel.preprocessing

import breeze.linalg.{*, DenseVector}
import io.picnicml.doddlemodel.data.Feature.FeatureIndex
import io.picnicml.doddlemodel.data.{Features, RealVector}
import io.picnicml.doddlemodel.typeclasses.Transformer

case class Binarizer private (private val thresholds: RealVector,
                              private val featureIndex: FeatureIndex)

object Binarizer {

  def apply(threshold: Double, featureIndex: FeatureIndex): Binarizer = {
    val numNumeric: Int = featureIndex.numerical.columnIndices.length
    require(numNumeric > 0, "There must be at least 1 numeric column in the given data")
    val thresholdsExtended = DenseVector.fill(numNumeric) {threshold}
    new Binarizer(thresholdsExtended, featureIndex)
  }

  def apply(thresholds: RealVector, featureIndex: FeatureIndex): Binarizer = {
    val numNumeric = featureIndex.numerical.columnIndices.length
    require(numNumeric > 0, "There must be at least 1 numeric column in the given data")
    require(numNumeric == thresholds.length, "A threshold should be given for every numerical column")
    new Binarizer(thresholds, featureIndex)
  }

  implicit lazy val ev: Transformer[Binarizer] = new Transformer[Binarizer] {

    override def fit(model: Binarizer, x: Features): Binarizer = model

    override protected def transformSafe(model: Binarizer, x: Features): Features = {
      val numericColsOnly = x(::, model.featureIndex.numerical.columnIndices).toDenseMatrix
      (numericColsOnly(*, ::) >:> model.thresholds).mapValues((v: Boolean) => if (v) 1.0 else 0.0)
    }

    override def isFitted(model: Binarizer): Boolean = true
  }
}