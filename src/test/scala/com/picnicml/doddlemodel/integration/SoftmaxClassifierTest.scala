package com.picnicml.doddlemodel.integration

import com.picnicml.doddlemodel.data.loadIrisDataset
import com.picnicml.doddlemodel.linear.SoftmaxClassifier
import com.picnicml.doddlemodel.metrics.accuracy
import com.picnicml.doddlemodel.modelselection.CrossValidation
import org.scalatest.{FlatSpec, Matchers}

class SoftmaxClassifierTest extends FlatSpec with Matchers {

  "Softmax classifier" should "achieve a reasonable score on the iris dataset" in {
    val (x, y) = loadIrisDataset
    val model = SoftmaxClassifier()
    val cv = CrossValidation(accuracy, folds = 10)

    cv.score(model, x, y) should be > 0.94
  }
}
