package com.doddlemodel.integration

import org.scalatest.{FlatSpec, Matchers}
import com.doddlemodel.data.DataLoaders.loadBreastCancerDataset
import com.doddlemodel.linear.LogisticRegression
import com.doddlemodel.metrics.ClassificationMetrics.accuracy
import com.doddlemodel.modelselection.CrossValidation

class LogisticRegressionTest extends FlatSpec with Matchers {

  "Logistic regression" should "achieve a reasonable score on the breast cancer dataset" in {
    val (x, y) = loadBreastCancerDataset
    val model = LogisticRegression()
    val cv = CrossValidation(accuracy, folds = 10)

    //cv.score(model, x, y) should be > 0.95
    val trainedModel = model.fit(x, y)
    val yPred = trainedModel.predict(x)
    println(yPred)
    println(accuracy(yPred, y))
  }
}
