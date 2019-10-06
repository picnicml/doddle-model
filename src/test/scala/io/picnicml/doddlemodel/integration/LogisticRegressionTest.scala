package io.picnicml.doddlemodel.integration

import io.picnicml.doddlemodel.CrossScalaCompat.floatOrdering
import io.picnicml.doddlemodel.data.loadBreastCancerDataset
import io.picnicml.doddlemodel.linear.LogisticRegression
import io.picnicml.doddlemodel.metrics.accuracy
import io.picnicml.doddlemodel.modelselection.{CrossValidation, KFoldSplitter}
import org.scalatest.{FlatSpec, Matchers}

import scala.util.Random

class LogisticRegressionTest extends FlatSpec with Matchers {

  "Logistic regression" should "achieve a reasonable score on the breast cancer dataset" in {
    val (x, y, _) = loadBreastCancerDataset
    val model = LogisticRegression()
    val cv = CrossValidation(accuracy, KFoldSplitter(numFolds = 10))
    implicit val rand: Random = new Random(42)

    cv.score(model, x, y) should be > 0.92f
  }
}
