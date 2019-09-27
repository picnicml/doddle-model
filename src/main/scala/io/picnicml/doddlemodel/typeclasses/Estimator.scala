package io.picnicml.doddlemodel.typeclasses

import java.io.{FileOutputStream, ObjectOutputStream}

// evidence needs to be serializable because it is persisted along with the actual
// estimators within the io.picnicml.doddlemodel.pipeline.Pipeline
trait Estimator[A] extends Serializable {

  def isFitted(model: A): Boolean

  def save(model: A, filePath: String): Unit = {
    val outputStream = new ObjectOutputStream(new FileOutputStream(filePath))
    outputStream.writeObject(model)
    outputStream.close()
  }
}
