package io.picnicml.doddlemodel.syntax

object OptionSyntax {

  implicit class OptionOps[A](val parameter: Option[A]) extends AnyVal {

    def getOrBreak: A =
      parameter.getOrElse(
        throw new IllegalStateException(s"$parameter should be defined because the estimator is already trained"))
  }
}
