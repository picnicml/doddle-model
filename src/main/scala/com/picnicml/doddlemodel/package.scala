package com.picnicml

import java.io.{FileInputStream, ObjectInputStream}

import com.picnicml.doddlemodel.typeclasses.Estimator

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
}
