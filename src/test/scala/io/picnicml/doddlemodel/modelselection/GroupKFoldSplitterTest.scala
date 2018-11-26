package io.picnicml.doddlemodel.modelselection

import breeze.linalg.DenseVector
import io.picnicml.doddlemodel.TestingUtils
import org.scalatest.{FlatSpec, Matchers}

class GroupKFoldSplitterTest extends FlatSpec with Matchers with TestingUtils {

  "GroupKFoldSplitter" should "split data so that folds are i.i.d" in {
    val (x, y) = dummyData(10)
    val groups = DenseVector(1, 2, 2, 0, 0, 0, 2, 1, 1, 2)
    val splitter = GroupKFoldSplitter(numFolds = 3)
    val splits = splitter.splitData(x, y, groups)

    val noGroupsInTrainTestSplits = splits.forall { split =>
      val trGroups = split.yTr.map(x => groups(x.toInt)).toArray
      val teGroups = split.yTe.map(x => groups(x.toInt)).toArray
      trGroups.forall(trGroup => !teGroups.contains(trGroup))
    }

    noGroupsInTrainTestSplits shouldBe true
  }
}
