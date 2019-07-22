package io.picnicml.doddlemodel.modelselection

import java.util.concurrent.Executors

import io.picnicml.doddlemodel.maxNumThreads

import scala.concurrent.ExecutionContext

case class CrossValReusable(yes: Boolean)

/** A custom execution context that is suitable for training models in parallel (multiple cross validation folds). */
private[modelselection] class CVExecutionContext extends ExecutionContext {

  private val threadPool = Executors.newFixedThreadPool(maxNumThreads)

  override def execute(runnable: Runnable): Unit = {
    val _ = threadPool.submit(runnable)
  }
  override def reportFailure(cause: Throwable): Unit = throw cause

  def shutdownNow(): Unit = {
    val notExecuted = threadPool.shutdownNow()
    require(notExecuted.isEmpty, "CVExecutionContext received shutdownNow before the tasks were completed")
  }
}
