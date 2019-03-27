package io.picnicml.doddlemodel.data

import breeze.linalg.sum
import io.picnicml.doddlemodel.data.Feature.NumericalFeature
import org.scalatest.{FlatSpec, Matchers}

class ResourceDatasetLoadersTest extends FlatSpec with Matchers {

  "Data loaders" should "load the boston housing prices dataset" in {
    val (x, y, featureIndex) = loadBostonDataset
    x.rows shouldBe 506
    x.cols shouldBe 13
    y.length shouldBe x.rows
    featureIndex.names shouldBe IndexedSeq(
      "crim",
      "zn",
      "indus",
      "chas",
      "nox",
      "rm",
      "age",
      "dis",
      "rad",
      "tax",
      "ptratio",
      "black",
      "lstat"
    )
    featureIndex.types shouldBe (0 until 13).map(_ => NumericalFeature)
    featureIndex.columnIndices shouldBe (0 until 13)
  }

  it should "load the breast cancer dataset" in {
    val (x, y, featureIndex) = loadBreastCancerDataset
    x.rows shouldBe 569
    x.cols shouldBe 30
    y.length shouldBe x.rows
    sum(y) shouldBe 357.0
    featureIndex.names shouldBe IndexedSeq(
      "mean_radius",
      "mean_texture",
      "mean_perimeter",
      "mean_area",
      "mean_smoothness",
      "mean_compactness",
      "mean_concavity",
      "mean_concave_points",
      "mean_symmetry",
      "mean_fractal_dimension",
      "radius_error",
      "texture_error",
      "perimeter_error",
      "area_error",
      "smoothness_error",
      "compactness_error",
      "concavity_error",
      "concave_points_error",
      "symmetry_error",
      "fractal_dimension_error",
      "worst_radius",
      "worst_texture",
      "worst_perimeter",
      "worst_area",
      "worst_smoothness",
      "worst_compactness",
      "worst_concavity",
      "worst_concave_points",
      "worst_symmetry",
      "worst_fractal_dimension"
    )
    featureIndex.types shouldBe (0 until 30).map(_ => NumericalFeature)
    featureIndex.columnIndices shouldBe (0 until 30)
  }

  it should "load the iris dataset" in {
    val (x, y, featureIndex) = loadIrisDataset
    x.rows shouldBe 150
    x.cols shouldBe 4
    y.length shouldBe x.rows
    sum(y(0 until 50)) shouldBe 0.0
    sum(y(50 until 100)) shouldBe 50.0
    sum(y(100 until 150)) shouldBe 100.0
    featureIndex.names shouldBe IndexedSeq(
      "sepal_length",
      "sepal_width",
      "petal_length",
      "petal_width"
    )
    featureIndex.types shouldBe (0 until 4).map(_ => NumericalFeature)
    featureIndex.columnIndices shouldBe (0 until 4)
  }

  it should "load the high school test dataset" in {
    val (x, y, featureIndex) = loadHighSchoolTestDataset
    x.rows shouldBe 500
    x.cols shouldBe 6
    y.length shouldBe x.rows
    featureIndex.names shouldBe IndexedSeq(
      "math_avg",
      "language1_avg",
      "language2_avg",
      "physics_avg",
      "geography_avg",
      "history_avg"
    )
    featureIndex.types shouldBe (0 until 6).map(_ => NumericalFeature)
    featureIndex.columnIndices shouldBe (0 until 6)
  }
}
