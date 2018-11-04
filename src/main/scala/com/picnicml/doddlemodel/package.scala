package com.picnicml

import java.io.{FileInputStream, ObjectInputStream}

import com.picnicml.doddlemodel.data.{Features, Target}
import com.picnicml.doddlemodel.typeclasses.{Estimator, Predictor, Transformer}

import scala.language.implicitConversions

package object doddlemodel {

  lazy val maxNumThreads: Int =
    System.getProperty("maxNumThreads", Runtime.getRuntime.availableProcessors.toString).toInt

  def loadEstimator[A](filePath: String)(implicit ev: Estimator[A]): A = {
    val inputStream = new ObjectInputStream(new FileInputStream(filePath))
    val instance = inputStream.readObject.asInstanceOf[A]
    inputStream.close()
    instance
  }

  // wrapper classes that can hold arbitrary instances along with evidence
  // useful when creating heterogenous lists of instances that all have implicit
  // implicit evidence in scope, e.g.
  // val transformers = List[Transformable[_]](StandardScaler(), ...)
  case class Transformable[A](model: A, ev: Transformer[A]) {
    def isFitted: Boolean = ev.isFitted(model)
    def fit(x: Features): Transformable[A] = this.copy(model = ev.fit(model, x))
    def transform(x: Features): Features = ev.transform(model, x)
  }

  implicit def asTransformable[A](model: A)(implicit ev: Transformer[A]): Transformable[A] =
    Transformable(model, ev)

  case class Predictable[A](model: A, ev: Predictor[A]) {
    def isFitted: Boolean = ev.isFitted(model)
    def fit(x: Features, y: Target): Predictable[A] = this.copy(model = ev.fit(model, x, y))
    def predict(x: Features): Target = ev.predict(model, x)
  }

  implicit def asPredictable[A](model: A)(implicit ev: Predictor[A]): Predictable[A] =
    Predictable(model, ev)
}
