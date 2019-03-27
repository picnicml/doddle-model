package io.picnicml.doddlemodel.dummy.classification

import breeze.linalg.sum
import io.picnicml.doddlemodel.data.{loadBreastCancerDataset, loadIrisDataset}
import io.picnicml.doddlemodel.dummy.classification.MostFrequentClassifier.ev
import org.scalatest.{FlatSpec, Matchers, OptionValues}

class MostFrequentClassifierTest extends FlatSpec with Matchers with OptionValues {

  "Most frequent classifier" should "infer the most frequent class from the iris dataset" in {
    val (x, y, _) = loadIrisDataset
    val model = MostFrequentClassifier()
    val trainedModel = ev.fit(model, x, y)
    trainedModel.mostFrequentClass.value shouldBe 0.0
    sum(ev.predict(trainedModel, x)) shouldBe 0.0
  }

  it should "infer the most frequent class from the breast cancer dataset" in {
    val (x, y, _) = loadBreastCancerDataset
    val model = MostFrequentClassifier()
    val trainedModel = ev.fit(model, x, y)
    trainedModel.mostFrequentClass.value shouldBe 1.0
    sum(ev.predict(trainedModel, x)) shouldBe x.rows.toDouble
  }
}
