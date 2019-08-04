package io.picnicml.doddlemodel.preprocessing

import breeze.linalg.{*, DenseVector}
import io.picnicml.doddlemodel.data.Feature.FeatureIndex
import io.picnicml.doddlemodel.data.{Features, RealVector}
import io.picnicml.doddlemodel.typeclasses.Transformer

case class Binarizer private (private val thresholds: RealVector, private val featureIndex: FeatureIndex) {
  private val numNumeric = featureIndex.numerical.columnIndices.length
  require(numNumeric == 0 || numNumeric == thresholds.length, "A threshold should be given for every numerical column")
}

object Binarizer {

  def apply(threshold: Double, featureIndex: FeatureIndex): Binarizer = {
    val numNumeric: Int = featureIndex.numerical.columnIndices.length
    val thresholdsExtended = DenseVector.fill(numNumeric) {threshold}
    Binarizer(thresholdsExtended, featureIndex)
  }

  implicit lazy val ev: Transformer[Binarizer] = new Transformer[Binarizer] {

    override def isFitted(model: Binarizer): Boolean = true

    override def fit(model: Binarizer, x: Features): Binarizer = model

    override protected def transformSafe(model: Binarizer, x: Features): Features = {
      val xCopy = x.copy
      val numericColIndices = model.featureIndex.numerical.columnIndices
      // only perform binarization if there are numerical columns, otherwise keep input
      if(numericColIndices.nonEmpty) {
        val numericColsOnly = x(::, numericColIndices).toDenseMatrix
        xCopy(::, numericColIndices) := (numericColsOnly(*, ::) >:> model.thresholds).mapValues((v: Boolean) =>
          if (v) 1.0 else 0.0)
      }

      xCopy
    }
  }
}
