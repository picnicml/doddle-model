package io.picnicml.doddlemodel.preprocessing

import breeze.linalg.{*, DenseMatrix, DenseVector, convert}
import breeze.stats.{mean, stddev}
import io.picnicml.doddlemodel.TestingUtils
import io.picnicml.doddlemodel.data.Feature.{CategoricalFeature, FeatureIndex, NumericalFeature}
import io.picnicml.doddlemodel.preprocessing.StandardScaler.ev
import org.scalactic.{Equality, TolerantNumerics}
import org.scalatest.{FlatSpec, Matchers}

class StandardScalerTest extends FlatSpec with Matchers with TestingUtils {

  implicit val tolerance: Equality[Float] = TolerantNumerics.tolerantFloatEquality(1e-4f)

  "Standard scaler" should "preprocess the numerical features" in {
    val x = DenseMatrix.rand[Float](10, 5, rand = randomUniform)
    val featureIndex = FeatureIndex(
      List(
        NumericalFeature,
        NumericalFeature,
        NumericalFeature,
        NumericalFeature,
        CategoricalFeature
      )
    )
    val scaler = StandardScaler(featureIndex)
    val trainedScaler = ev.fit(scaler, x)
    val xTransformed = ev.transform(trainedScaler, x)

    breezeEqual(mean(x(::, *)).t, DenseVector.zeros[Float](5)) shouldBe false
    breezeEqual(convert(stddev(x(::, *)).t, Float), DenseVector.ones[Float](5)) shouldBe false

    val expectedMeans = DenseVector.zeros[Float](5)
    expectedMeans(-1) = mean(x(::, -1))
    breezeEqual(mean(xTransformed(::, *)).t, expectedMeans) shouldBe true

    val expectedStdDevs = DenseVector.ones[Float](5)
    expectedStdDevs(-1) = stddev(x(::, -1)).toFloat
    breezeEqual(convert(stddev(xTransformed(::, *)).t, Float), expectedStdDevs) shouldBe true
  }

  it should "handle the zero variance case" in {
    val x = DenseMatrix.ones[Float](10, 5)
    val scaler = StandardScaler(FeatureIndex.numerical(5))
    val trainedScaler = ev.fit(scaler, x)
    val xTransformed = ev.transform(trainedScaler, x)

    xTransformed.forall(_.isNaN) shouldBe false
  }

  it should "preprocess a subset of numerical features" in {
    val x = DenseMatrix.rand[Float](10, 5, rand = randomUniform)
    val scaler = StandardScaler(FeatureIndex.numerical(5).subset("f0", "f2", "f4"))
    val trainedScaler = ev.fit(scaler, x)
    val xTransformed = ev.transform(trainedScaler, x)

    breezeEqual(mean(x(::, *)).t, DenseVector.zeros[Float](5)) shouldBe false
    breezeEqual(convert(stddev(x(::, *)).t, Float), DenseVector.ones[Float](5)) shouldBe false

    assert(tolerance.areEqual(mean(xTransformed(::, 0)), 0.0f))
    assert(tolerance.areEqual(convert(stddev(xTransformed(::, 0)), Float), 1.0f))
    assert(!tolerance.areEqual(mean(xTransformed(::, 1)), 0.0f))
    assert(!tolerance.areEqual(convert(stddev(xTransformed(::, 1)), Float), 1.0f))
    assert(tolerance.areEqual(mean(xTransformed(::, 2)), 0.0f))
    assert(tolerance.areEqual(convert(stddev(xTransformed(::, 2)), Float), 1.0f))
    assert(!tolerance.areEqual(mean(xTransformed(::, 3)), 0.0f))
    assert(!tolerance.areEqual(convert(stddev(xTransformed(::, 3)), Float), 1.0f))
    assert(tolerance.areEqual(mean(xTransformed(::, 4)), 0.0f))
    assert(tolerance.areEqual(convert(stddev(xTransformed(::, 4)), Float), 1.0f))
  }
}
