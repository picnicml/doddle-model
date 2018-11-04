package com.picnicml.doddlemodel.syntax

import com.picnicml.doddlemodel.data.{Features, Target}
import com.picnicml.doddlemodel.typeclasses.Regressor

object RegressorSyntax {

  implicit class RegressorOps[A](model: A) {

    def isFitted(implicit ev: Regressor[A]): Boolean = ev.isFitted(model)

    def fit(x: Features, y: Target)(implicit ev: Regressor[A]): A = ev.fit(model, x, y)

    def predict(x: Features)(implicit ev: Regressor[A]): Target = ev.predict(model, x)

    def save(filePath: String)(implicit ev: Regressor[A]): Unit = ev.save(model, filePath)
  }
}
