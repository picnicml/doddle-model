package com.picnicml.doddlemodel.dummy.classification

import breeze.linalg.sum
import com.picnicml.doddlemodel.data.{loadBreastCancerDataset, loadIrisDataset}
import com.picnicml.doddlemodel.dummy.classification.MostFrequentClassifier.ev
import org.scalatest.{FlatSpec, Matchers}

class MostFrequentClassifierTest extends FlatSpec with Matchers {

  "Most frequent classifier" should "infer the most frequent class from the iris dataset" in {
    val (x, y) = loadIrisDataset
    val model = MostFrequentClassifier()
    val trainedModel = ev.fit(model, x, y)
    trainedModel.mostFrequentClass.get shouldBe 0.0
    sum(ev.predict(trainedModel, x)) shouldBe 0.0
  }

  it should "infer the most frequent class from the breast cancer dataset" in {
    val (x, y) = loadBreastCancerDataset
    val model = MostFrequentClassifier()
    val trainedModel = ev.fit(model, x, y)
    trainedModel.mostFrequentClass.get shouldBe 1.0
    sum(ev.predict(trainedModel, x)) shouldBe x.rows.toDouble
  }
}
