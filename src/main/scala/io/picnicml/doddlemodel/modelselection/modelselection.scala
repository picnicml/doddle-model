package io.picnicml.doddlemodel.modelselection

import java.util.concurrent.Executors

import io.picnicml.doddlemodel.maxNumThreads
import io.picnicml.doddlemodel.typeclasses.Predictor

import scala.concurrent.ExecutionContext

private[modelselection] case class CVFold[A: Predictor](predictor: A, score: Float, crossValId: Int)

/** A custom execution context that is suitable for training models in parallel (at most maxNumThreads running). */
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
