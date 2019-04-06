package io.picnicml.doddlemodel.preprocessing

import io.picnicml.doddlemodel.data.Features
import io.picnicml.doddlemodel.typeclasses.Transformer

case class OneHotEncoder()

object OneHotEncoder {

  implicit lazy val ev: Transformer[OneHotEncoder] = new Transformer[OneHotEncoder] {

    override def isFitted(model: OneHotEncoder): Boolean = ???

    override def fit(model: OneHotEncoder, x: Features): OneHotEncoder = ???

    override protected def transformSafe(model: OneHotEncoder, x: Features): Features = ???
  }
}
