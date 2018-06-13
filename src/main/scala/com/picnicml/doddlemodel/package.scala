package com.picnicml

import java.io.{FileInputStream, ObjectInputStream}

import com.picnicml.doddlemodel.base.{Estimator, Predictor, Transformer}

import scala.language.existentials

package object doddlemodel {

  // the final type AnyX will not change regardless of what A is
  type AnyTransformer = A forSome { type A <: Transformer[A] }
  type AnyPredictor = A forSome { type A <: Predictor[A] }

  lazy val maxNumThreads: Int =
    System.getProperty("maxNumThreads", Runtime.getRuntime.availableProcessors.toString).toInt

  def loadEstimator[A <: Estimator](filePath: String): A = {
    val inputStream = new ObjectInputStream(new FileInputStream(filePath))
    val instance = inputStream.readObject.asInstanceOf[A]
    inputStream.close()
    instance
  }
}
