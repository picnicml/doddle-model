package com.picnicml.doddlemodel.dummy.classification

import breeze.linalg.DenseVector
import com.picnicml.doddlemodel.typeclasses.Classifier
import com.picnicml.doddlemodel.data.{Features, Simplex, Target}

/** An immutable dummy classifier that always predicts the most frequent label.
  *
  * Examples:
  * val model = MostFrequentClassifier()
  */
@SerialVersionUID(1L)
class MostFrequentClassifier private (val numClasses: Option[Int], val mostFrequentClass: Option[Double])
  extends Classifier[MostFrequentClassifier] with Serializable {

  override def isFitted: Boolean = this.mostFrequentClass.isDefined

  override protected def copy(numClasses: Int): MostFrequentClassifier =
    new MostFrequentClassifier(Some(numClasses), None)

  override protected def fitSafe(x: Features, y: Target): MostFrequentClassifier = {
    val mostFrequentClass = y.activeValuesIterator.foldLeft(Map[Double, Int]()) { (acc, x) =>
      if (acc.contains(x)) acc + (x -> (acc(x) + 1)) else acc + (x -> 1)
    }.toArray.sortBy(_._1).maxBy(_._2)._1

    new MostFrequentClassifier(this.numClasses, Some(mostFrequentClass))
  }

  override protected def predictSafe(x: Features): Target =
    DenseVector(Array.fill(x.rows)(this.mostFrequentClass.get))

  override protected def predictProbaSafe(x: Features): Simplex = throw new NotImplementedError()
}

object MostFrequentClassifier {

  def apply(): MostFrequentClassifier = new MostFrequentClassifier(None, None)
}
