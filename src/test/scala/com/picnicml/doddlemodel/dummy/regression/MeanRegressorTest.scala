package com.picnicml.doddlemodel.dummy.regression

import com.picnicml.doddlemodel.data.loadBostonDataset
import org.scalatest.{FlatSpec, Matchers}

class MeanRegressorTest extends FlatSpec with Matchers {

  "Mean regressor" should "infer the correct mean from the boston housing dataset" in {
    val (x, y) = loadBostonDataset
    val model = MeanRegressor()
    val trainedModel = model.fit(x, y)
    trainedModel.mean.get shouldBe 22.532806324110666
    trainedModel.predict(x).toArray.forall(_ == 22.532806324110666) shouldBe true
  }
}
