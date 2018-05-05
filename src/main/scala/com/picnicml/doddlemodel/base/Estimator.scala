package com.picnicml.doddlemodel.base

import java.io.{FileOutputStream, ObjectOutputStream, Serializable}

import com.picnicml.doddlemodel.data.{Features, Target}

abstract class Estimator[A <: Estimator[A]] {
  this: Serializable =>

  def isFitted: Boolean

  def fit(x: Features, y: Target): A

  def save(filePath: String): Unit = {
    val outputStream = new ObjectOutputStream(new FileOutputStream(filePath))
    outputStream.writeObject(this)
    outputStream.close()
  }
}
