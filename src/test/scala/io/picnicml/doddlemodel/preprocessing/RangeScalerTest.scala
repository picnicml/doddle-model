package io.picnicml.doddlemodel.preprocessing

import breeze.linalg.DenseMatrix
import io.picnicml.doddlemodel.TestingUtils
import io.picnicml.doddlemodel.data.Feature.{CategoricalFeature, FeatureIndex, NumericalFeature}
import org.scalatest.{FlatSpec, Matchers}
import io.picnicml.doddlemodel.preprocessing.RangeScaler.ev
import org.scalactic.{Equality, TolerantNumerics}

class RangeScalerTest extends FlatSpec with Matchers with TestingUtils {
  implicit val doubleTolerance: Equality[Double] = TolerantNumerics.tolerantDoubleEquality(1e-4)

  "Range scaler" should "scale features to specified range" in {
    val xMatrix: DenseMatrix[Double] = DenseMatrix(
      List(-3.0, 2.0, 1.0),
      List(-3.0, 3.0, 0.0),
      List(-3.0, 0.0, 0.0),
      List(-3.0, 5.0, 1.0)
    )
    val featureIndex = FeatureIndex(List(NumericalFeature, NumericalFeature, CategoricalFeature))
    val rangeScaler = RangeScaler((0.0, 1.0), featureIndex)
    val trainedRangeScaler = ev.fit(rangeScaler, xMatrix)
    breezeEqual(ev.transform(trainedRangeScaler, xMatrix), DenseMatrix(
      List(0.0, 0.4),
      List(0.0, 0.6),
      List(0.0, 0.0),
      List(0.0, 1.0)
    )) shouldBe true
  }

}
