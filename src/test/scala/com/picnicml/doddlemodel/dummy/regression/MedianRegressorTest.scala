package com.picnicml.doddlemodel.dummy.regression

import com.picnicml.doddlemodel.data.loadBostonDataset
import org.scalatest.{FlatSpec, Matchers}

class MedianRegressorTest extends FlatSpec with Matchers {

  "Median regressor" should "infer the correct median from the boston housing dataset" in {
    val (x, y) = loadBostonDataset
    val model = MedianRegressor()
    val trainedModel = model.fit(x, y)
    trainedModel.median.get shouldBe 21.199999999999999
    trainedModel.predict(x).toArray.forall(_ == 21.199999999999999) shouldBe true
  }
}
