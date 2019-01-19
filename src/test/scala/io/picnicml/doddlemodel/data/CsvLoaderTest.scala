package io.picnicml.doddlemodel.data

import breeze.linalg.{DenseMatrix, DenseVector}
import io.picnicml.doddlemodel.TestingUtils
import io.picnicml.doddlemodel.data.ResourceDatasetLoaders.loadDummyCsvReadingDataset
import org.scalatest.{FlatSpec, Matchers}

class CsvLoaderTest extends FlatSpec with Matchers with TestingUtils {

  "Csv loader" should "load and encode data correctly" in {
    val (x, y) = loadDummyCsvReadingDataset
    val xCorrect = DenseMatrix(
      List(0.0, 0.0, 0.1, 1.1),
      List(1.0, Double.NaN, 0.2, 1.2),
      List(2.0, 1.0, 0.3, Double.NaN),
      List(3.0, 2.0, 0.4, 1.4),
      List(0.0, 0.0, 0.1, 1.1),
      List(3.0, Double.NaN, 0.4, 1.4),
    )
    val yCorrect = DenseVector(0.0, 1.0, 2.0, 3.0, 0.0, 3.0)
    breezeEqual(x, xCorrect) shouldBe true
    breezeEqual(y, yCorrect) shouldBe true
  }
}
