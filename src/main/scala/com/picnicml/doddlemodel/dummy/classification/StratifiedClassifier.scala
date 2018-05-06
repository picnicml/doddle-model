package com.picnicml.doddlemodel.dummy.classification

import breeze.linalg.DenseVector
import breeze.stats.distributions.Multinomial
import com.picnicml.doddlemodel.base.Classifier
import com.picnicml.doddlemodel.data.{Features, RealVector, Simplex, Target}

@SerialVersionUID(1L)
class StratifiedClassifier private (val numClasses: Option[Int], targetDistr: Option[Multinomial[RealVector, Int]])
  extends Classifier[StratifiedClassifier] with Serializable {

  override def isFitted: Boolean = this.targetDistr.isDefined

  override protected def copy(numClasses: Int): StratifiedClassifier =
    new StratifiedClassifier(Some(numClasses), this.targetDistr)

  override protected def fitSafe(x: Features, y: Target): StratifiedClassifier = {
    val probs = y.activeValuesIterator.foldLeft(Map[Double, Int]()) { (acc, x) =>
      if (acc.contains(x)) acc + (x -> (acc(x) + 1)) else acc + (x -> 1)
    }.toArray.sortBy(_._1).map(_._2 / y.length.toDouble)

    new StratifiedClassifier(this.numClasses, Some(Multinomial[RealVector, Int](DenseVector(probs))))
  }

  override protected def predictSafe(x: Features): Target =
    DenseVector(Array.range(0, x.rows).map(_ => this.targetDistr.get.draw.toDouble))

  override protected def predictProbaSafe(x: Features): Simplex = throw new NotImplementedError()

  def getTargetDistributionParams: RealVector = {
    require(this.isFitted, "Called getTargetDistributionParams on a model that is not trained yet")
    targetDistr.get.params.copy
  }
}

object StratifiedClassifier {

  def apply(): StratifiedClassifier = new StratifiedClassifier(None, None)
}
