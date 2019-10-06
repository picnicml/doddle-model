package io.picnicml.doddlemodel.preprocessing

import breeze.linalg.{*, Axis, DenseMatrix, Vector, convert, max}
import cats.syntax.option._
import io.picnicml.doddlemodel.data.Feature.FeatureIndex
import io.picnicml.doddlemodel.data.Features
import io.picnicml.doddlemodel.syntax.OptionSyntax._
import io.picnicml.doddlemodel.typeclasses.Transformer


/** An immutable preprocessor that encodes categorical features as a one-hot (dummy) matrix made up of binary columns.
  * Preprocessor expects that categorical input values are in range [0, max(values)). If during the transformation
  * a value larger than during training is encountered it is ignored, i.e. no value is set in the binary encoded matrix.
  * Transformed categorical columns are appended at the end of the feature matrix.
  *
  * @param featureIndex feature index associated with features, this is needed so that only categorical features
  *                     are transformed by this preprocessor, could be a subset of columns to be transformed
  *
  * Examples:
  * val encoder = OneHotEncoder(featureIndex)
  * val encoderSubsetOfColumns = OneHotEncoder(featureIndex.subset("f0", "f2"))
  */
case class OneHotEncoder private (private val numBinaryColumns: Option[Vector[Int]],
                                  private val featureIndex: FeatureIndex)

object OneHotEncoder {

  def apply(featureIndex: FeatureIndex): OneHotEncoder = OneHotEncoder(none, featureIndex)

  @SerialVersionUID(0L)
  implicit lazy val ev: Transformer[OneHotEncoder] = new Transformer[OneHotEncoder] {

    @inline override def isFitted(model: OneHotEncoder): Boolean = model.numBinaryColumns.isDefined

    override def fit(model: OneHotEncoder, x: Features): OneHotEncoder = {
      val numBinaryColumns = convert(max(x(::, model.featureIndex.categorical.columnIndices).apply(::, *)).t, Int) + 1
      model.copy(numBinaryColumns = numBinaryColumns.some)
    }

    override protected def transformSafe(model: OneHotEncoder, x: Features): Features = {
      val xTransformed = model.featureIndex.categorical.columnIndices.zipWithIndex.foldLeft(x) {
        case (xTransformedCurrent, (colIndex, statisticIndex)) =>
          appendEncodedColumns(xTransformedCurrent, colIndex, model.numBinaryColumns.getOrBreak(statisticIndex))
      }
      xTransformed.delete(model.featureIndex.categorical.columnIndices, Axis._1)
    }

    private def appendEncodedColumns(x: Features, columnIndex: Int, numEncodedColumns: Int): Features = {
      val encoded = DenseMatrix.zeros[Float](x.rows, numEncodedColumns)
      convert(x(::, columnIndex), Int).iterator.foreach { case (rowIndex, colIndex) =>
        // if value is larger than the maximum value encountered during training it is ignored,
        // i.e. no value is set in the binary encoded matrix
        if (colIndex < numEncodedColumns) encoded(rowIndex, colIndex) = 1.0f
      }
      DenseMatrix.horzcat(x, encoded)
    }
  }
}
