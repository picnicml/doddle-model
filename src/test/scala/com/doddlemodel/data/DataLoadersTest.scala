package com.doddlemodel.data

import org.scalatest.{FlatSpec, Matchers}
import com.doddlemodel.data.DataLoaders._

class DataLoadersTest extends FlatSpec with Matchers {

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
  }
}
