package io.picnicml.doddlemodel.syntax

import io.picnicml.doddlemodel.data.{Features, Target}
import io.picnicml.doddlemodel.typeclasses.Regressor

object RegressorSyntax {

  implicit class RegressorOps[A](val model: A) extends AnyVal {

    def isFitted(implicit ev: Regressor[A]): Boolean = ev.isFitted(model)

    def fit(x: Features, y: Target)(implicit ev: Regressor[A]): A = ev.fit(model, x, y)

    def predict(x: Features)(implicit ev: Regressor[A]): Target = ev.predict(model, x)

    def save(filePath: String)(implicit ev: Regressor[A]): Unit = ev.save(model, filePath)
  }
}
