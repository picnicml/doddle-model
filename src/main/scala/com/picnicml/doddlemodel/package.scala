package com.picnicml

import java.io.{FileInputStream, ObjectInputStream}

import com.picnicml.doddlemodel.base.Estimator

package object doddlemodel {

  def loadEstimator[T <: Estimator](filePath: String): T = {
    val inputStream = new ObjectInputStream(new FileInputStream(filePath))
    val instance = inputStream.readObject.asInstanceOf[T]
    inputStream.close()
    instance
  }
}
