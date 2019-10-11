package io.picnicml.doddlemodel.dummy.classification

import breeze.linalg.DenseVector
import breeze.stats.distributions.Multinomial
import cats.syntax.option._
import io.picnicml.doddlemodel.CrossScalaCompat.doubleOrdering
import io.picnicml.doddlemodel.data.{Features, Simplex, Target}
import io.picnicml.doddlemodel.dummy.classification.StratifiedClassifier.ev
import io.picnicml.doddlemodel.syntax.OptionSyntax._
import io.picnicml.doddlemodel.typeclasses.Classifier

/** An immutable dummy classifier that samples predictions from a stratified categorical distribution.
  *
  * Examples:
  * val model = StratifiedClassifier()
  */
case class StratifiedClassifier private (numClasses: Option[Int],
                                         targetDistr: Option[Multinomial[DenseVector[Double], Int]]) {

  def getTargetDistributionParams: DenseVector[Double] = {
    require(ev.isFitted(this), "Called getTargetDistributionParams on a model that is not trained yet")
    this.targetDistr.getOrBreak.params.copy
  }
}

object StratifiedClassifier {

  def apply(): StratifiedClassifier = StratifiedClassifier(none, none)

  @SerialVersionUID(0L)
  implicit lazy val ev: Classifier[StratifiedClassifier] = new Classifier[StratifiedClassifier] {

    override def numClasses(model: StratifiedClassifier): Option[Int] = model.numClasses

    override def isFitted(model: StratifiedClassifier): Boolean = model.targetDistr.isDefined

    override protected[doddlemodel] def copy(model: StratifiedClassifier, numClasses: Int): StratifiedClassifier =
      model.copy(numClasses = numClasses.some)

    override protected def fitSafe(model: StratifiedClassifier, x: Features, y: Target): StratifiedClassifier = {
      val probs = y.activeValuesIterator.foldLeft(Map[Double, Int]()) { (acc, value) =>
        val valueDouble = value.toDouble
        if (acc.contains(valueDouble)) acc + (valueDouble -> (acc(valueDouble) + 1)) else acc + (valueDouble -> 1)
      }.toArray.sortBy(_._1).map(_._2 / y.length.toDouble)

      model.copy(targetDistr = Multinomial[DenseVector[Double], Int](DenseVector(probs)).some)
    }

    override protected def predictSafe(model: StratifiedClassifier, x: Features): Target =
      DenseVector(Array.fill(x.rows)(model.targetDistr.getOrBreak.draw.toFloat))

    override protected def predictProbaSafe(model: StratifiedClassifier, x: Features): Simplex =
      throw new NotImplementedError("Method predictProbaSafe is not defined for StratifiedClassifier")
  }
}
