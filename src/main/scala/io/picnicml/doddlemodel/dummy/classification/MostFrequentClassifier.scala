package io.picnicml.doddlemodel.dummy.classification

import breeze.linalg.DenseVector
import cats.syntax.option._
import io.picnicml.doddlemodel.CrossScalaCompat.floatOrdering
import io.picnicml.doddlemodel.data.{Features, Simplex, Target}
import io.picnicml.doddlemodel.typeclasses.Classifier

/** An immutable dummy classifier that always predicts the most frequent label.
  *
  * Examples:
  * val model = MostFrequentClassifier()
  */
case class MostFrequentClassifier private (numClasses: Option[Int], mostFrequentClass: Option[Float])

object MostFrequentClassifier {

  def apply(): MostFrequentClassifier = MostFrequentClassifier(none, none)

  @SerialVersionUID(0L)
  implicit lazy val ev: Classifier[MostFrequentClassifier] = new Classifier[MostFrequentClassifier] {

    override def numClasses(model: MostFrequentClassifier): Option[Int] = model.numClasses

    override def isFitted(model: MostFrequentClassifier): Boolean = model.mostFrequentClass.isDefined

    override protected[doddlemodel] def copy(model: MostFrequentClassifier, numClasses: Int): MostFrequentClassifier =
      model.copy(numClasses = numClasses.some)

    override protected def fitSafe(model: MostFrequentClassifier, x: Features, y: Target): MostFrequentClassifier = {
      val mostFrequentClass = y.activeValuesIterator.foldLeft(Map[Float, Int]()) { (acc, x) =>
        if (acc.contains(x)) acc + (x -> (acc(x) + 1)) else acc + (x -> 1)
      }.toArray.sortBy(_._1).maxBy(_._2)._1

      model.copy(mostFrequentClass = mostFrequentClass.some)
    }

    override protected def predictSafe(model: MostFrequentClassifier, x: Features): Target =
      DenseVector(Array.fill(x.rows)(model.mostFrequentClass.get))

    override protected def predictProbaSafe(model: MostFrequentClassifier, x: Features): Simplex =
      throw new NotImplementedError("Method predictProbaSafe is not defined for MostFrequentClassifier")
  }
}
