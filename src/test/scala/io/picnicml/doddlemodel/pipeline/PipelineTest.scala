package io.picnicml.doddlemodel.pipeline

import io.picnicml.doddlemodel.data.loadBreastCancerDataset
import io.picnicml.doddlemodel.impute.MeanValueImputer
import io.picnicml.doddlemodel.linear.LogisticRegression
import io.picnicml.doddlemodel.metrics.accuracy
import io.picnicml.doddlemodel.pipeline.Pipeline.{ev, pipe}
import io.picnicml.doddlemodel.preprocessing.StandardScaler
import org.scalatest.{FlatSpec, Matchers}

class PipelineTest extends FlatSpec with Matchers {

  "Pipeline" should "implement the isFitted function correctly" in {
    val (x, y, _) = loadBreastCancerDataset
    val pipeline = initializePipeline
    ev.isFitted(pipeline) shouldBe false
    val trainedPipeline = ev.fit(pipeline, x, y)
    ev.isFitted(trainedPipeline) shouldBe true
  }

  it should "apply the transformation steps correctly" in {
    val (x, y, _) = loadBreastCancerDataset
    val trainedPipeline = ev.fit(initializePipeline, x, y)
    accuracy(y, ev.predict(trainedPipeline, x)) should be > 0.98
  }

  private def initializePipeline: Pipeline = {
    val transformers: PipelineTransformers = List(
      pipe(MeanValueImputer()),
      pipe(StandardScaler())
    )
    Pipeline(transformers)(pipe(LogisticRegression()))
  }
}
