package io.picnicml.doddlemodel.dummy.regression

import io.picnicml.doddlemodel.data.loadBostonDataset
import io.picnicml.doddlemodel.dummy.regression.MedianRegressor.ev
import org.scalatest.{FlatSpec, Matchers, OptionValues}

class MedianRegressorTest extends FlatSpec with Matchers with OptionValues {

  "Median regressor" should "infer median from the boston housing dataset" in {
    val (x, y, _) = loadBostonDataset
    val model = MedianRegressor()
    val trainedModel = ev.fit(model, x, y)
    trainedModel.median.value shouldBe 21.2f
    ev.predict(trainedModel, x).toArray.forall(_ == 21.2f) shouldBe true
  }
}
