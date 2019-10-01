package io.picnicml.doddlemodel.preprocessing

import breeze.linalg.DenseVector
import io.picnicml.doddlemodel.data.Feature.{CategoricalFeature, FeatureIndex}
import io.picnicml.doddlemodel.data.{Features, RealVector}
import io.picnicml.doddlemodel.typeclasses.Transformer

case class Binarizer(private val thresholds: RealVector, private val featureIndex: FeatureIndex) {
  private val numNumeric = featureIndex.numerical.columnIndices.length
  require(numNumeric == 0 || numNumeric == thresholds.length, "A threshold should be given for every numerical column")
}

/** An immutable preprocessor that binarizes numerical features according to a threshold.
  * Numerical feature values that are greater than the threshold are set to `1.0`, while those that are lower or equal
  * are set to `0.0`. Non-numerical features are left untouched.
  * */
object Binarizer {

  /** Create a binarizer where a single threshold is applied to all numerical columns.
    *
    * @param threshold threshold to be applied
    * @param featureIndex feature index associated with features - this is needed so that only numerical features are
    *                     transformed by this preprocessor; could be a subset of columns to be transformed
    *
    * @example Binarize a matrix with two features: one numerical and one categorical.
    *   {{{
    *     import io.picnicml.doddlemodel.preprocessing.Binarizer.ev
    *
    *     val featureIndex = FeatureIndex(List(NumericalFeature, CategoricalFeature))
    *     val x = DenseMatrix(
    *       List(1.0, 0.0),
    *       List(-1.0, 1.0),
    *       List(2.0, 0.0)
    *     )
    *     // equivalently, DenseVector(0.0) could be used
    *     val threshold = 0.0
    *     val binarizer = Binarizer(threshold, featureIndex)
    *     // Note: no fitting required
    *     val xTransformed = ev.transform(binarizer, x)
    *   }}}
    */
  def apply(threshold: Double, featureIndex: FeatureIndex): Binarizer = {
    val numNumeric: Int = featureIndex.numerical.columnIndices.length
    val thresholdsExtended = DenseVector.fill(numNumeric) { threshold }
    Binarizer(thresholdsExtended, featureIndex)
  }

  implicit lazy val ev: Transformer[Binarizer] = new Transformer[Binarizer] {

    override def isFitted(model: Binarizer): Boolean = true

    override def fit(model: Binarizer, x: Features): Binarizer = model

    override protected def featureIndexSafe(model: Binarizer): FeatureIndex = {
      // on-the-fly generation of modified feature index so that fitting is not required
      val numFeatures = model.featureIndex.columnIndices.size
      val types = List.fill(numFeatures) { CategoricalFeature }
      val names = model.featureIndex.names.toList
      val indices = (0 until numFeatures).toList
      FeatureIndex(names, types, indices)
    }

    override protected def transformSafe(model: Binarizer, x: Features): Features = {
      val xCopy = x.copy
      model.featureIndex.numerical.columnIndices.zipWithIndex.foreach {
        case (colIndex, thresholdIndex) => (0 until xCopy.rows).foreach {
          rowIndex =>
            xCopy(rowIndex, colIndex) = if (xCopy(rowIndex, colIndex) > model.thresholds(thresholdIndex)) 1.0 else 0.0
        }
      }

      xCopy
    }
  }
}
