package io.picnicml.doddlemodel.pipeline

import io.picnicml.doddlemodel.CrossScalaCompat.floatOrdering
import io.picnicml.doddlemodel.data.Feature.FeatureIndex
import io.picnicml.doddlemodel.data.loadBreastCancerDataset
import io.picnicml.doddlemodel.impute.MeanValueImputer
import io.picnicml.doddlemodel.linear.LogisticRegression
import io.picnicml.doddlemodel.metrics.accuracy
import io.picnicml.doddlemodel.pipeline.Pipeline.{ev, pipe}
import io.picnicml.doddlemodel.preprocessing.StandardScaler
import org.scalatest.{FlatSpec, Matchers}

class PipelineTest extends FlatSpec with Matchers {

  "Pipeline" should "implement the isFitted function" in {
    val (x, y, featureIndex) = loadBreastCancerDataset
    val pipeline = initializePipeline(featureIndex)
    ev.isFitted(pipeline) shouldBe false
    val trainedPipeline = ev.fit(pipeline, x, y)
    ev.isFitted(trainedPipeline) shouldBe true
  }

  it should "apply the transformation steps" in {
    val (x, y, featureIndex) = loadBreastCancerDataset
    val trainedPipeline = ev.fit(initializePipeline(featureIndex), x, y)
    accuracy(y, ev.predict(trainedPipeline, x)) should be > 0.98f
  }

  private def initializePipeline(featureIndex: FeatureIndex): Pipeline = {
    val transformers: PipelineTransformers = List(
      pipe(MeanValueImputer(featureIndex)),
      pipe(StandardScaler(featureIndex))
    )
    Pipeline(transformers)(pipe(LogisticRegression()))
  }
}
