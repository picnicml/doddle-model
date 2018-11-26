package io.picnicml.doddlemodel.typeclasses

import java.io.{FileOutputStream, ObjectOutputStream}

trait Estimator[A] {

  def isFitted(model: A): Boolean

  def save(model: A, filePath: String): Unit = {
    val outputStream = new ObjectOutputStream(new FileOutputStream(filePath))
    outputStream.writeObject(model)
    outputStream.close()
  }
}
