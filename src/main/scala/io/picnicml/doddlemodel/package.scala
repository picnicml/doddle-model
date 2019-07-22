package io.picnicml

import java.io.{FileInputStream, ObjectInputStream}

import io.picnicml.doddlemodel.typeclasses.Estimator

package object doddlemodel {

  lazy val maxNumThreads: Int =
    System.getProperty("maxNumThreads", Runtime.getRuntime.availableProcessors.toString).toInt

  def loadEstimator[A: Estimator](filePath: String): A = {
    val inputStream = new ObjectInputStream(new FileInputStream(filePath))
    val instance = inputStream.readObject.asInstanceOf[A]
    inputStream.close()
    instance
  }
}
