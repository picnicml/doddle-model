package com.picnicml

import java.io.{FileInputStream, ObjectInputStream}

import com.picnicml.doddlemodel.base.Estimator

package object doddlemodel {

  lazy val maxNumThreads: Int =
    System.getProperty("maxNumThreads", Runtime.getRuntime.availableProcessors.toString).toInt

  def loadEstimator[A <: Estimator[A]](filePath: String): A = {
    val inputStream = new ObjectInputStream(new FileInputStream(filePath))
    val instance = inputStream.readObject.asInstanceOf[A]
    inputStream.close()
    instance
  }
}
