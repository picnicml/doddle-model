package io.picnicml.doddlemodel.data

import breeze.linalg.{DenseMatrix, DenseVector}
import io.picnicml.doddlemodel.TestingUtils
import io.picnicml.doddlemodel.data.Feature.{CategoricalFeature, NumericalFeature}
import io.picnicml.doddlemodel.data.ResourceDatasetLoaders.loadDummyCsvReadingDataset
import org.scalatest.{FlatSpec, Matchers}

class CsvLoaderTest extends FlatSpec with Matchers with TestingUtils {

  "Csv loader" should "load and encode data" in {
    val (x, y, featureIndex) = loadDummyCsvReadingDataset
    val xCorrect = DenseMatrix(
      List(0.0f, 0.0f, 0.1f, 1.1f),
      List(1.0f, Float.NaN, 0.2f, 1.2f),
      List(2.0f, 1.0f, 0.3f, Float.NaN),
      List(3.0f, 2.0f, 0.4f, 1.4f),
      List(0.0f, 0.0f, 0.1f, 1.1f),
      List(3.0f, Float.NaN, 0.4f, 1.4f)
    )
    val yCorrect = DenseVector(0.0f, 1.0f, 2.0f, 3.0f, 0.0f, 3.0f)
    breezeEqual(x, xCorrect) shouldBe true
    breezeEqual(y, yCorrect) shouldBe true
    featureIndex.names shouldBe IndexedSeq("f0", "f1", "f2", "f3")
    featureIndex.types shouldBe IndexedSeq(
      CategoricalFeature,
      CategoricalFeature,
      NumericalFeature,
      NumericalFeature
    )
    featureIndex.columnIndices shouldBe (0 until 4)
  }
}
