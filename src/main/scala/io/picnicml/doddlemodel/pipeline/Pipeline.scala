package io.picnicml.doddlemodel.pipeline

import io.picnicml.doddlemodel.data.{Features, Target}
import io.picnicml.doddlemodel.pipeline.Pipeline.{Predictable, Transformable}
import io.picnicml.doddlemodel.typeclasses.{Predictor, Transformer}

/** An immutable pipeline of multiple transformers and a single predictor applied as the very last step.
  *
  * Examples:
  * val pipeline = Pipeline(List(pipe(StandardScaler()), ...))(pipe(LogisticRegression()))
  */
case class Pipeline private (predictor: Predictable[_], transformers: List[Transformable[_]])

object Pipeline {

  def apply(transformers: List[Transformable[_]])(predictor: Predictable[_]): Pipeline =
    Pipeline(predictor, transformers)

  @SerialVersionUID(0L)
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

  // wrapper classes that can hold arbitrary instances along with evidence useful when
  // creating heterogenous lists of instances that all have implicit evidence in scope
  case class Transformable[A](model: A, ev: Transformer[A]) {
    def isFitted: Boolean = ev.isFitted(model)
    def fit(x: Features): Transformable[A] = this.copy(model = ev.fit(model, x))
    def transform(x: Features): Features = ev.transform(model, x)
  }

  def pipe[A](model: A)(implicit ev: Transformer[A]): Transformable[A] =
    Transformable(model, ev)

  case class Predictable[A](model: A, ev: Predictor[A]) {
    def isFitted: Boolean = ev.isFitted(model)
    def fit(x: Features, y: Target): Predictable[A] = this.copy(model = ev.fit(model, x, y))
    def predict(x: Features): Target = ev.predict(model, x)
  }

  def pipe[A](model: A)(implicit ev: Predictor[A]): Predictable[A] =
    Predictable(model, ev)
}
