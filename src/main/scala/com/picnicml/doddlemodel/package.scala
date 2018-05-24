package com.picnicml

import java.io.{FileInputStream, ObjectInputStream}
import java.util.concurrent.Executors

import com.picnicml.doddlemodel.base.Estimator

import scala.concurrent.ExecutionContext

package object doddlemodel {

  private lazy val maxNumThreads: Int = System.getProperty("maxNumThreads", "1").toInt
  implicit val executionContext: ExecutionContext =
    ExecutionContext.fromExecutor(Executors.newFixedThreadPool(maxNumThreads))

  def loadEstimator[A <: Estimator[A]](filePath: String): A = {
    val inputStream = new ObjectInputStream(new FileInputStream(filePath))
    val instance = inputStream.readObject.asInstanceOf[A]
    inputStream.close()
    instance
  }
}
