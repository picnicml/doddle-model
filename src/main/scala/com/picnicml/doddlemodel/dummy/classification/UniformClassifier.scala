package com.picnicml.doddlemodel.dummy.classification

import breeze.linalg.DenseVector
import breeze.stats.distributions.Rand
import com.picnicml.doddlemodel.base.Classifier
import com.picnicml.doddlemodel.data.{Features, Simplex, Target}

/** An immutable dummy classifier that samples predictions from a uniform categorical distribution.
  *
  * Examples:
  * val model = UniformClassifier()
  */
@SerialVersionUID(1L)
class UniformClassifier private (val numClasses: Option[Int]) extends Classifier[UniformClassifier] with Serializable {

  override def isFitted: Boolean = this.numClasses.isDefined

  override protected def copy(numClasses: Int): UniformClassifier = new UniformClassifier(Some(numClasses))

  override protected def fitSafe(x: Features, y: Target): UniformClassifier = this

  override protected def predictSafe(x: Features): Target =
    DenseVector.rand(x.rows, Rand.randInt(this.numClasses.get))

  override protected def predictProbaSafe(x: Features): Simplex = throw new NotImplementedError()
}

object UniformClassifier {

  def apply(): UniformClassifier = new UniformClassifier(None)
}
