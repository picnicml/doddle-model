package com.picnicml.doddlemodel.dummy.regression

import com.picnicml.doddlemodel.data.loadBostonDataset
import com.picnicml.doddlemodel.dummy.regression.MeanRegressor.ev
import org.scalatest.{FlatSpec, Matchers}

class MeanRegressorTest extends FlatSpec with Matchers {

  "Mean regressor" should "infer the correct mean from the boston housing dataset" in {
    val (x, y) = loadBostonDataset
    val model = MeanRegressor()
    val trainedModel = ev.fit(model, x, y)
    trainedModel.mean.get shouldBe 22.532806324110666
    ev.predict(trainedModel, x).toArray.forall(_ == 22.532806324110666) shouldBe true
  }
}
