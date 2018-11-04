package com.picnicml.doddlemodel.pipeline

import com.picnicml.doddlemodel.data.{Features, Target}
import com.picnicml.doddlemodel.typeclasses.Predictor
import com.picnicml.doddlemodel.{Predictable, Transformable}

/** An immutable pipeline of multiple transformers and a single predictor applied as the very last step.
  *
  * Examples:
  * val preprocessingSteps = List[Transformable[_]](StandardScaler(), ...)
  * val pipeline = Pipeline(preprocessingSteps)(LogisticRegression())
  */
case class Pipeline private (predictor: Predictable[_], transformers: List[Transformable[_]])

object Pipeline {

  def apply(transformers: List[Transformable[_]])(predictor: Predictable[_]): Pipeline =
    Pipeline(predictor, transformers)

  implicit lazy val ev: Predictor[Pipeline] = new Predictor[Pipeline] {

    override def isFitted(model: Pipeline): Boolean =
      model.transformers.forall(_.isFitted) && model.predictor.isFitted

    override def fit(model: Pipeline, x: Features, y: Target): Pipeline = {
      object TransformResult { def apply(): TransformResult = TransformResult(x, List[Transformable[_]]()) }
      case class TransformResult(xTransformed: Features, fittedTransformers: List[Transformable[_]])

      val transformResult = model.transformers.foldLeft(TransformResult()) {
        case (TransformResult(currentX, fittedTransformers), currentTransformer) =>
          val fittedTransformer = currentTransformer.fit(currentX)
          val xTransformed = fittedTransformer.transform(currentX)
          TransformResult(xTransformed, fittedTransformer :: fittedTransformers)
      }

      Pipeline(model.predictor.fit(transformResult.xTransformed, y), transformResult.fittedTransformers.reverse)
    }

    override protected def predictSafe(model: Pipeline, x: Features): Target = {
      val xTransformedResult = model.transformers.foldLeft(x) {
        case (xTransformed, currentTransformer) => currentTransformer.transform(xTransformed)
      }
      model.predictor.predict(xTransformedResult)
    }
  }
}
