package io.picnicml.doddlemodel.dummy.regression

import io.picnicml.doddlemodel.data.loadBostonDataset
import io.picnicml.doddlemodel.dummy.regression.MeanRegressor.ev
import org.scalatest.OptionValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class MeanRegressorTest extends AnyFlatSpec with Matchers with OptionValues {

  "Mean regressor" should "infer mean from the boston housing dataset" in {
    val (x, y, _) = loadBostonDataset
    val model = MeanRegressor()
    val trainedModel = ev.fit(model, x, y)
    trainedModel.mean.value shouldBe 22.532804f
    ev.predict(trainedModel, x).toArray.forall(_ == 22.532804f) shouldBe true
  }
}
