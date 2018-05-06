package com.picnicml.doddlemodel.dummy.classification

import breeze.linalg.DenseVector
import com.picnicml.doddlemodel.TestUtils
import com.picnicml.doddlemodel.data.{loadBreastCancerDataset, loadIrisDataset}
import org.scalactic.{Equality, TolerantNumerics}
import org.scalatest.{FlatSpec, Matchers}

class StratifiedClassifierTest extends FlatSpec with Matchers with TestUtils {

  implicit val doubleTolerance: Equality[Double] = TolerantNumerics.tolerantDoubleEquality(1e-3)

  "Stratified classifier" should "infer a correct categorical distribution on the iris dataset" in {
    val (x, y) = loadIrisDataset
    val model = StratifiedClassifier()
    val trainedModel = model.fit(x, y)
    breezeEqual(trainedModel.getTargetDistributionParams, DenseVector(0.333, 0.333, 0.333)) shouldBe true
  }

  it should "infer a correct categorical distribution on the breast cancer dataset" in {
    val (x, y) = loadBreastCancerDataset
    val model = StratifiedClassifier()
    val trainedModel = model.fit(x, y)
    breezeEqual(trainedModel.getTargetDistributionParams, DenseVector(0.372, 0.627)) shouldBe true
  }
}
