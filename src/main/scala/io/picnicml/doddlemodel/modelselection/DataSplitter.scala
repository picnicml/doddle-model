package io.picnicml.doddlemodel.modelselection

import io.picnicml.doddlemodel.CrossScalaCompat.LazyListCompat
import io.picnicml.doddlemodel.data.{Features, IntVector, Target, TrainTestSplit}

import scala.util.Random

trait DataSplitter {

  def splitData(x: Features, y: Target)(implicit rand: Random): LazyListCompat[TrainTestSplit]

  def splitData(x: Features, y: Target, groups: IntVector)(implicit rand: Random): LazyListCompat[TrainTestSplit]
}
