package com.picnicml.doddlemodel.integration

import breeze.stats.distributions.RandBasis
import com.picnicml.doddlemodel.data.loadBreastCancerDataset
import com.picnicml.doddlemodel.linear.LogisticRegression
import com.picnicml.doddlemodel.metrics.accuracy
import com.picnicml.doddlemodel.modelselection.CrossValidation
import org.scalatest.{FlatSpec, Matchers}

class LogisticRegressionTest extends FlatSpec with Matchers {

  implicit val randBasis: RandBasis = RandBasis.withSeed(0)

  "Logistic regression" should "achieve a reasonable score on the breast cancer dataset" in {
    val (x, y) = loadBreastCancerDataset
    val model = LogisticRegression()
    val cv = CrossValidation[LogisticRegression](accuracy, folds = 10)

    cv.score(model, x, y) should be > 0.93
  }
}
