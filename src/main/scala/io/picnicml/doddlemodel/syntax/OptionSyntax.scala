package io.picnicml.doddlemodel.syntax

object OptionSyntax {

  implicit class OptionOps[A](parameter: Option[A]) {

    def getOrBreak: A =
      parameter.getOrElse(throw new IllegalStateException(s"$parameter should be defined in this state of the model"))
  }
}
