package io.picnicml.doddlemodel.dummy.classification

import breeze.linalg.{DenseVector, convert}
import io.picnicml.doddlemodel.TestingUtils
import io.picnicml.doddlemodel.data.{loadBreastCancerDataset, loadIrisDataset}
import io.picnicml.doddlemodel.dummy.classification.StratifiedClassifier.ev
import org.scalactic.{Equality, TolerantNumerics}
import org.scalatest.{FlatSpec, Matchers}

class StratifiedClassifierTest extends FlatSpec with Matchers with TestingUtils {

  implicit val tolerance: Equality[Float] = TolerantNumerics.tolerantFloatEquality(1e-3f)

  "Stratified classifier" should "infer a categorical distribution from the iris dataset" in {
    val (x, y, _) = loadIrisDataset
    val model = StratifiedClassifier()
    val trainedModel = ev.fit(model, x, y)
    breezeEqual(
      convert(trainedModel.getTargetDistributionParams, Float),
      DenseVector(0.333f, 0.333f, 0.333f)
    ) shouldBe true
  }

  it should "infer a categorical distribution from the breast cancer dataset" in {
    val (x, y, _) = loadBreastCancerDataset
    val model = StratifiedClassifier()
    val trainedModel = ev.fit(model, x, y)
    breezeEqual(
      convert(trainedModel.getTargetDistributionParams, Float),
      DenseVector(0.372f, 0.627f)
    ) shouldBe true
  }
}
