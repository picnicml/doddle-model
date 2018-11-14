package com.picnicml.doddlemodel

import java.util.concurrent.Executors

import com.picnicml.doddlemodel.data.{Features, Target}

import scala.concurrent.ExecutionContext

package object modelselection {

  case class TrainTestSplit(xTr: Features, yTr: Target, xTe: Features, yTe: Target)

  case class CrossValReusable(yes: Boolean)

  /** A custom execution context that is suitable for training models in parallel (multiple cross validation folds). */
  private[modelselection] class CVExecutionContext extends ExecutionContext {

    private val threadPool = Executors.newFixedThreadPool(maxNumThreads)

    override def execute(runnable: Runnable): Unit = threadPool.submit(runnable)
    override def reportFailure(cause: Throwable): Unit = throw cause

    def shutdownNow(): Unit = {
      val notExecuted = threadPool.shutdownNow()
      require(notExecuted.isEmpty, "CVExecutionContext received shutdownNow before the tasks were completed")
    }
  }
}
