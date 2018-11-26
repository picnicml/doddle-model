package io.picnicml.doddlemodel.syntax

import io.picnicml.doddlemodel.data.Features
import io.picnicml.doddlemodel.typeclasses.Transformer

object TransformerSyntax {

  implicit class TransformerOps[A](model: A) {

    def isFitted(implicit ev: Transformer[A]): Boolean = ev.isFitted(model)

    def fit(x: Features)(implicit ev: Transformer[A]): A = ev.fit(model, x)

    def transform(x: Features)(implicit ev: Transformer[A]): Features = ev.transform(model, x)

    def save(filePath: String)(implicit ev: Transformer[A]): Unit = ev.save(model, filePath)
  }
}
