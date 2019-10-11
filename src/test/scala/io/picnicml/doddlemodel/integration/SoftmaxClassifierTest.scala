package io.picnicml.doddlemodel.integration

import io.picnicml.doddlemodel.CrossScalaCompat.floatOrdering
import io.picnicml.doddlemodel.data.loadIrisDataset
import io.picnicml.doddlemodel.linear.SoftmaxClassifier
import io.picnicml.doddlemodel.metrics.accuracy
import io.picnicml.doddlemodel.modelselection.{CrossValidation, KFoldSplitter}
import org.scalatest.{FlatSpec, Matchers}

import scala.util.Random

class SoftmaxClassifierTest extends FlatSpec with Matchers {

  "Softmax classifier" should "achieve a reasonable score on the iris dataset" in {
    val (x, y, _) = loadIrisDataset
    val model = SoftmaxClassifier()
    val cv = CrossValidation(accuracy, KFoldSplitter(numFolds = 10))
    implicit val rand: Random = new Random(42)

    cv.score(model, x, y) should be > 0.93f
  }
}
