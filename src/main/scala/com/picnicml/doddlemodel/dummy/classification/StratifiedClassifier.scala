//package com.picnicml.doddlemodel.dummy.classification
//
//import breeze.stats.distributions.Multinomial
//import com.picnicml.doddlemodel.base.Classifier
//import com.picnicml.doddlemodel.data.{Features, RealVector, Simplex, Target}
//
//@SerialVersionUID(1L)
//class StratifiedClassifier private (val numClasses: Option[Int], targetDistr: Option[Multinomial[RealVector, Double]])
//  extends Classifier[StratifiedClassifier] with Serializable {
//
//  override def isFitted: Boolean = numClasses.isDefined && targetDistr.isDefined
//
//
//  /** A function that creates a new classifier with numClasses set. */
//  override protected def copy(numClasses: Int): StratifiedClassifier = ???
//
//  override def fitSafe(x: Features, y: Target): StratifiedClassifier = {
//    this
//    //new StratifiedClassifier(Some(), Some())
//  }
//
//  override def predict(x: Features): Target = ???
//
//  override def predictProba(x: Features): Simplex = ???
//}
//
//object StratifiedClassifier {
//
//  def apply(): StratifiedClassifier = new StratifiedClassifier(None, None)
//}
