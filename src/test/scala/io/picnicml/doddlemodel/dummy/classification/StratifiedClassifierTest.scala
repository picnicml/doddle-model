package io.picnicml.doddlemodel.dummy.classification

import breeze.linalg.DenseVector
import io.picnicml.doddlemodel.TestingUtils
import io.picnicml.doddlemodel.data.{loadBreastCancerDataset, loadIrisDataset}
import io.picnicml.doddlemodel.dummy.classification.StratifiedClassifier.ev
import org.scalactic.{Equality, TolerantNumerics}
import org.scalatest.{FlatSpec, Matchers}

class StratifiedClassifierTest extends FlatSpec with Matchers with TestingUtils {

  implicit val doubleTolerance: Equality[Double] = TolerantNumerics.tolerantDoubleEquality(1e-3)

  "Stratified classifier" should "infer a correct categorical distribution from the iris dataset" in {
    val (x, y, _) = loadIrisDataset
    val model = StratifiedClassifier()
    val trainedModel = ev.fit(model, x, y)
    breezeEqual(trainedModel.getTargetDistributionParams, DenseVector(0.333, 0.333, 0.333)) shouldBe true
  }

  it should "infer a correct categorical distribution from the breast cancer dataset" in {
    val (x, y, _) = loadBreastCancerDataset
    val model = StratifiedClassifier()
    val trainedModel = ev.fit(model, x, y)
    breezeEqual(trainedModel.getTargetDistributionParams, DenseVector(0.372, 0.627)) shouldBe true
  }
}
