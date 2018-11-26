package io.picnicml.doddlemodel.syntax

import io.picnicml.doddlemodel.data.{Features, Target}
import io.picnicml.doddlemodel.typeclasses.Predictor

object PredictorSyntax {

  implicit class PredictorOps[A](model: A) {

    def isFitted(implicit ev: Predictor[A]): Boolean = ev.isFitted(model)

    def fit(x: Features, y: Target)(implicit ev: Predictor[A]): A = ev.fit(model, x, y)

    def predict(x: Features)(implicit ev: Predictor[A]): Target = ev.predict(model, x)

    def save(filePath: String)(implicit ev: Predictor[A]): Unit = ev.save(model, filePath)
  }
}
