package com.picnicml.doddlemodel.modelselection
import com.picnicml.doddlemodel.data.{Features, IntVector, Target}

import scala.util.Random

class GroupKFoldSplitter private (val folds: Int) extends DataSplitter {

  override def splitData(x: Features, y: Target, groups: IntVector)
                        (implicit rand: Random = new Random()): Stream[TrainTestSplit] = {
    ???
  }

  override def splitData(x: Features, y: Target)(implicit rand: Random): Stream[TrainTestSplit] =
    throw new NotImplementedError("GroupKFoldSplitter only splits data based on groups")
}


object GroupKFoldSplitter {

  def apply(folds: Int): GroupKFoldSplitter = {
    require(folds > 0, "Number of folds must be positive")
    new GroupKFoldSplitter(folds)
  }
}
