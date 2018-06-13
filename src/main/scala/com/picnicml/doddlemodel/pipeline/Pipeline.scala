package com.picnicml.doddlemodel.pipeline

import com.picnicml.doddlemodel.base.Predictor
import com.picnicml.doddlemodel.data.{Features, Target}
import com.picnicml.doddlemodel.{AnyPredictor, AnyTransformer}

import scala.language.existentials

/** An immutable pipeline of multiple transformers and a single predictor applied as the very last step.
  *
  * Examples:
  * val pipeline = Pipeline(StandardScaler(), ...)(LogisticRegression())
  */
@SerialVersionUID(1L)
class Pipeline private (val predictor: AnyPredictor, val transformers: AnyTransformer*)
  extends Predictor[Pipeline] with Serializable {

  override def isFitted: Boolean =
    this.transformers.forall(_.isFitted) && this.predictor.isFitted

  override def fit(x: Features, y: Target): Pipeline = {
    case class TransformResult(xTransformed: Features, fittedTransformers: List[AnyTransformer])
    object TransformResult { def apply(): TransformResult = TransformResult(x, List[AnyTransformer]()) }

    val transformResult = this.transformers.foldLeft(TransformResult()) {
      case (TransformResult(currentX, fittedTransformers), currentTransformer) =>
        val fittedTransformer = currentTransformer.fit(currentX)
        val xTransformed = fittedTransformer.transform(currentX)
        TransformResult(xTransformed, fittedTransformer :: fittedTransformers)
    }

    new Pipeline(this.predictor.fit(transformResult.xTransformed, y),
      transformResult.fittedTransformers.reverse:_*)
  }

  override protected def predictSafe(x: Features): Target = {
    val xTransformedResult = this.transformers.foldLeft(x) { case (xTransformed, currentTransformer) =>
      currentTransformer.transform(xTransformed)
    }
    this.predictor.predict(xTransformedResult)
  }
}

object Pipeline {

  def apply(transformers: AnyTransformer*)(predictor: AnyPredictor): Pipeline =
    new Pipeline(predictor, transformers:_*)
}
