package io.picnicml.doddlemodel.dummy.classification

import breeze.linalg.{DenseVector, convert}
import breeze.stats.distributions.Rand
import cats.syntax.option._
import io.picnicml.doddlemodel.data.{Features, Simplex, Target}
import io.picnicml.doddlemodel.typeclasses.Classifier

/** An immutable dummy classifier that samples predictions from a uniform categorical distribution.
  *
  * Examples:
  * val model = UniformClassifier()
  */
case class UniformClassifier private (numClasses: Option[Int])

object UniformClassifier {

  def apply(): UniformClassifier = UniformClassifier(none)

  @SerialVersionUID(0L)
  implicit lazy val ev: Classifier[UniformClassifier] = new Classifier[UniformClassifier] {

    override def numClasses(model: UniformClassifier): Option[Int] = model.numClasses

    override def isFitted(model: UniformClassifier): Boolean = model.numClasses.isDefined

    override protected[doddlemodel] def copy(model: UniformClassifier, numClasses: Int): UniformClassifier =
      model.copy(numClasses = numClasses.some)

    override protected def fitSafe(model: UniformClassifier, x: Features, y: Target): UniformClassifier =
      model.copy()

    override protected def predictSafe(model: UniformClassifier, x: Features): Target =
      convert(DenseVector.rand(x.rows, Rand.randInt(model.numClasses.get)), Float)

    override protected def predictProbaSafe(model: UniformClassifier, x: Features): Simplex =
      throw new NotImplementedError("Method predictProbaSafe is not defined for UniformClassifier")
  }
}
