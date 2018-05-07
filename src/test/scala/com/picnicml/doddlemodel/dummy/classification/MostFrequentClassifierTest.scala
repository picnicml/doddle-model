package com.picnicml.doddlemodel.dummy.classification

import com.picnicml.doddlemodel.data.{loadBreastCancerDataset, loadIrisDataset}
import org.scalatest.{FlatSpec, Matchers}

class MostFrequentClassifierTest extends FlatSpec with Matchers {

  "Most frequent classifier" should "infer the most frequent class from the iris dataset" in {
    val (x, y) = loadIrisDataset
    val model = MostFrequentClassifier()
    val trainedModel = model.fit(x, y)
    trainedModel.mostFrequentClass.get shouldBe 0.0
  }

  it should "infer the most frequent class from the breast cancer dataset" in {
    val (x, y) = loadBreastCancerDataset
    val model = MostFrequentClassifier()
    val trainedModel = model.fit(x, y)
    trainedModel.mostFrequentClass.get shouldBe 1.0
  }
}
