package com.picnicml.doddlemodel.base

import java.io.{FileOutputStream, ObjectOutputStream, Serializable}

abstract class Estimator {
  this: Serializable =>

  def isFitted: Boolean

  def save(filePath: String): Unit = {
    val outputStream = new ObjectOutputStream(new FileOutputStream(filePath))
    outputStream.writeObject(this)
    outputStream.close()
  }
}
