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

  it should "load the high school test dataset" in {
    val (x, y) = loadHighSchoolTestDataset
    x.rows shouldBe 500
    x.cols shouldBe 6
    y.length shouldBe x.rows
  }
}
