package com.picnicml.doddlemodel.pipeline

import com.picnicml.doddlemodel.data.loadBreastCancerDataset
import com.picnicml.doddlemodel.linear.LogisticRegression
import com.picnicml.doddlemodel.metrics.accuracy
import com.picnicml.doddlemodel.preprocessing.StandardScaler
import org.scalatest.{FlatSpec, Matchers}

class PipelineTest extends FlatSpec with Matchers {

  "Pipeline" should "implement the isFitted function correctly" in {
    val (x, y) = loadBreastCancerDataset
    val pipeline = Pipeline(StandardScaler())(LogisticRegression())
    pipeline.isFitted shouldBe false

    val trainedPipeline = pipeline.fit(x, y)
    trainedPipeline.isFitted shouldBe true
  }

  it should "apply the transformation steps correctly" in {
    // todo: improve this test when more transformers are implemented
    val (x, y) = loadBreastCancerDataset
    val pipeline = Pipeline(StandardScaler())(LogisticRegression())

    val trainedPipeline = pipeline.fit(x, y)
    accuracy(y, trainedPipeline.predict(x)) should be > 0.98
  }
}
