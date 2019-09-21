package io.picnicml.doddlemodel.preprocessing

import breeze.linalg.DenseVector
import io.picnicml.doddlemodel.CrossScalaCompat._
import io.picnicml.doddlemodel.data.Feature.FeatureIndex
import io.picnicml.doddlemodel.data.{Features, RealVector}
import io.picnicml.doddlemodel.typeclasses.Transformer

case class QuantileDiscretizer(private val numQuantiles: RealVector, private val featureIndex: FeatureIndex) {
  private val numNumeric = featureIndex.numerical.columnIndices.length
  require(numNumeric == 0 || numNumeric == numQuantiles.length, "A quantile should be given for every numerical column")
}

/** An immutable preprocessor that discretizes numerical features into buckets based on quantiles.
  * Numerical feature values are converted to categorical features based on their distribution
  * */
object QuantileDiscretizer {

  /** Create a quantile discretizer where data is split into quartiles.
    *
    * @param numQuantiles The number of quantiles desired
    * @param featureIndex feature index associated with features - this is needed so that only numerical features are
    *                     transformed by this preprocessor; could be a subset of columns to be transformed
    *
    * @example Quantile Discretize a matrix with two features: one numerical and one categorical.
    *   {{{
    *     import io.picnicml.doddlemodel.preprocessing.QuantileDiscretizer.ev
    *
    *     val featureIndex = FeatureIndex(List(NumericalFeature, CategoricalFeature))
    *     val x = DenseMatrix(
    *       List(-1.0, 0.0),
    *       List(0.0, 1.0),
    *       List(2.0, 0.0),
    *       List(5.0, 0.0)
    *     )
    *     // equivalently, DenseVector(4) could be used
    *     val numQuantiles = 4
    *     val discretizer = QuantileDiscretizer(numQuantiles, featureIndex)
    *     // Note: no fitting required
    *     val xTransformed = ev.transform(discretizer, x)
    *   }}}
    */
  def apply(numQuantiles: Int, featureIndex: FeatureIndex): QuantileDiscretizer = {
    val numNumeric = featureIndex.numerical.columnIndices.length
    val numQuantilesExtended: DenseVector[Double] = DenseVector.fill(numNumeric) { numQuantiles.toDouble }
    QuantileDiscretizer(numQuantilesExtended, featureIndex)
  }

  def splitEvenly[A](target: Seq[A], numQuantiles: RealVector, index: Int): Seq[Seq[A]] = {
    val numBuckets = numQuantiles(index).toInt
    val length = target.size
    val baseBucketSize =  length / numBuckets
    val remainder = length % numBuckets
    val sizeSplitPoint = length - remainder * (baseBucketSize + 1)
    val (smallerBuckets, biggerBuckets) = target.splitAt(sizeSplitPoint)
    val bucketIterator = smallerBuckets.grouped(baseBucketSize) ++ biggerBuckets.grouped((baseBucketSize + 1))
    bucketIterator.toSeq
  }

  implicit lazy val ev: Transformer[QuantileDiscretizer] = new Transformer[QuantileDiscretizer] {

    override def isFitted(model: QuantileDiscretizer): Boolean = true

    override def fit(model: QuantileDiscretizer, x: Features): QuantileDiscretizer = model

    override protected def transformSafe(model: QuantileDiscretizer, x: Features): Features = {
      val xCopy = x.copy
      model.featureIndex.numerical.columnIndices.zipWithIndex.foreach {
        case (colIndex, numQuantilesIndex) =>
          val colArray = xCopy(::, colIndex).toScalaVector.sorted
          val buckets = splitEvenly(colArray, model.numQuantiles, numQuantilesIndex)
          (0 until xCopy.rows).foreach {
            rowIndex => xCopy(rowIndex, colIndex) = buckets
              .indexWhere(_.contains(xCopy(rowIndex, colIndex)))
              .toDouble
          }
      }
      xCopy
    }
  }
}
