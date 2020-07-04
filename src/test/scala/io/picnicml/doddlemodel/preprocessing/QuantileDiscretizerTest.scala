package io.picnicml.doddlemodel.preprocessing

import breeze.linalg.{DenseMatrix, DenseVector}
import io.picnicml.doddlemodel.TestingUtils
import io.picnicml.doddlemodel.data.Feature.{CategoricalFeature, FeatureIndex, NumericalFeature}
import io.picnicml.doddlemodel.syntax.TransformerSyntax._
import org.scalatest.{FlatSpec, Matchers}

class QuantileDiscretizerTest extends FlatSpec with Matchers with TestingUtils {

  private val x = DenseMatrix(
    List(-1.0, -100.0, 0.0),
    List(0.0, -1.0, 1.0),
    List(2.0, 2000.0, 0.0),
    List(5.0, 20.0, 0.0)
  )

  "QuantileDiscretizer" should "bucket all numerical features into the number of buckets defined in bucketCounts" in {
    val featureIndex = FeatureIndex(List(NumericalFeature, NumericalFeature, CategoricalFeature))
    val bucketCounts: DenseVector[Double] = DenseVector(3, 4)

    val quantileDiscretizer = QuantileDiscretizer(bucketCounts, featureIndex).fit(x)
    val xQuantizedExpected = DenseMatrix(
      List(0.0, 0.0, 0.0),
      List(1.0, 1.0, 1.0),
      List(1.0, 3.0, 0.0),
      List(2.0, 2.0, 0.0)
    )

    breezeEqual(quantileDiscretizer.transform(x), xQuantizedExpected) shouldBe true
  }

  it should "process all the numerical columns by a single bucketCounts" in {
    val featureIndex = FeatureIndex(List(NumericalFeature, NumericalFeature, NumericalFeature))
    val bucketCount: Int = 2

    val quantileDiscretizer = QuantileDiscretizer(bucketCount, featureIndex).fit(x)
    val xQuantizedExpected = DenseMatrix(
      List(0.0, 0.0, 0.0),
      List(0.0, 0.0, 1.0),
      List(1.0, 1.0, 0.0),
      List(1.0, 1.0, 0.0)
    )

    breezeEqual(quantileDiscretizer.transform(x), xQuantizedExpected) shouldBe true
  }

  it should "amount to no-op if there are no numerical features in data" in {
    val featureIndex = FeatureIndex(List(CategoricalFeature, CategoricalFeature, CategoricalFeature))
    val bucketCounts: DenseVector[Double] = DenseVector(2, 3)
    val bucketCount: Int = 3

    val quantileDiscretizer1 = QuantileDiscretizer(bucketCounts, featureIndex).fit(x)
    val quantileDiscretizer2 = QuantileDiscretizer(bucketCount, featureIndex).fit(x)

    breezeEqual(quantileDiscretizer1.transform(x), x) shouldBe true
    breezeEqual(quantileDiscretizer2.transform(x), x) shouldBe true
  }

  it should "fail when the amount of passed bucketCounts is different to number of numerical features in data" in {
    val featureIndex = FeatureIndex(List(NumericalFeature, NumericalFeature, NumericalFeature))
    val bucketCounts: DenseVector[Double] = DenseVector(2, 3)

    // 3 numeric columns vs 2 thresholds
    an[IllegalArgumentException] should be thrownBy QuantileDiscretizer(bucketCounts, featureIndex)
  }
}
