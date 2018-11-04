package com.picnicml.doddlemodel.pipeline

import com.picnicml.doddlemodel.data.loadBreastCancerDataset
import com.picnicml.doddlemodel.linear.LogisticRegression
import com.picnicml.doddlemodel.metrics.accuracy
import com.picnicml.doddlemodel.pipeline.Pipeline.{ev, pipe}
import com.picnicml.doddlemodel.preprocessing.StandardScaler
import org.scalatest.{FlatSpec, Matchers}

class PipelineTest extends FlatSpec with Matchers {

  "Pipeline" should "implement the isFitted function correctly" in {
    val (x, y) = loadBreastCancerDataset
    val pipeline = Pipeline(List(pipe(StandardScaler())))(pipe(LogisticRegression()))
    ev.isFitted(pipeline) shouldBe false

    val trainedPipeline = ev.fit(pipeline, x, y)
    ev.isFitted(trainedPipeline) shouldBe true
  }

  it should "apply the transformation steps correctly" in {
    // todo: improve this test when more transformers are implemented
    val (x, y) = loadBreastCancerDataset
    val pipeline = Pipeline(List(pipe(StandardScaler())))(pipe(LogisticRegression()))
    val trainedPipeline = ev.fit(pipeline, x, y)
    accuracy(y, ev.predict(trainedPipeline, x)) should be > 0.98
  }
}
