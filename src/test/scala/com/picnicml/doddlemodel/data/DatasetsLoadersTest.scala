package com.picnicml.doddlemodel.data

import breeze.linalg.sum
import org.scalatest.{FlatSpec, Matchers}

class DatasetsLoadersTest extends FlatSpec with Matchers {

  "Data loaders" should "load the boston housing prices dataset" in {
    val (x, y) = loadBostonDataset
    x.rows shouldBe 506
    x.cols shouldBe 13
    y.length shouldBe x.rows
  }

  it should "load the breast cancer dataset" in {
    val (x, y) = loadBreastCancerDataset
    x.rows shouldBe 569
    x.cols shouldBe 30
    y.length shouldBe x.rows
    sum(y) shouldBe 357.0
  }

  it should "load the iris dataset" in {
    val (x, y) = loadIrisDataset
    x.rows shouldBe 150
    x.cols shouldBe 4
    y.length shouldBe x.rows
    sum(y(0 until 50)) shouldBe 0.0
    sum(y(50 until 100)) shouldBe 50.0
    sum(y(100 until 150)) shouldBe 100.0
  }

  it should "load the high school test dataset" in {
    val (x, y) = loadHighSchoolTestDataset
    x.rows shouldBe 500
    x.cols shouldBe 6
    y.length shouldBe x.rows
  }
}
