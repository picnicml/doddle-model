package com.picnicml.doddlemodel.syntax

import com.picnicml.doddlemodel.data.{Features, Target}
import com.picnicml.doddlemodel.typeclasses.Regressor

object RegressorSyntax {

  implicit class RegressorOps[A](regressor: A) {

    def fit(x: Features, y: Target)(implicit ev: Regressor[A]): A = ev.fit(regressor, x, y)

    def predict(x: Features)(implicit ev: Regressor[A]): Target = ev.predict(regressor, x)

    def isFitted(implicit ev: Regressor[A]): Boolean = ev.isFitted(regressor)

    def save(filePath: String)(implicit ev: Regressor[A]): Unit = ev.save(regressor, filePath)
  }
}
