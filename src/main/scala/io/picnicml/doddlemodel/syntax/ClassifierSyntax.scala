package io.picnicml.doddlemodel.syntax

import io.picnicml.doddlemodel.data.{Features, Simplex, Target}
import io.picnicml.doddlemodel.typeclasses.Classifier

object ClassifierSyntax {

  implicit class ClassifierOps[A](val model: A) extends AnyVal {

    def numClasses(implicit ev: Classifier[A]): Option[Int] = ev.numClasses(model)

    def isFitted(implicit ev: Classifier[A]): Boolean = ev.isFitted(model)

    def fit(x: Features, y: Target)(implicit ev: Classifier[A]): A = ev.fit(model, x, y)

    def predict(x: Features)(implicit ev: Classifier[A]): Target = ev.predict(model, x)

    def predictProba(x: Features)(implicit ev: Classifier[A]): Simplex = ev.predictProba(model, x)

    def save(filePath: String)(implicit ev: Classifier[A]): Unit = ev.save(model, filePath)
  }
}
