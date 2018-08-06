package com.picnicml.doddlemodel.typeclasses

import java.io.{FileOutputStream, ObjectOutputStream}

trait Estimator[A] {

  def isFitted(estimator: A): Boolean

  final def save(estimator: A, filePath: String): Unit = {
    val outputStream = new ObjectOutputStream(new FileOutputStream(filePath))
    outputStream.writeObject(estimator)
    outputStream.close()
  }
}
