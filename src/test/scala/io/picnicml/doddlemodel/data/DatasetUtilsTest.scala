package io.picnicml.doddlemodel.data

import breeze.linalg.DenseVector
import io.picnicml.doddlemodel.TestingUtils
import io.picnicml.doddlemodel.data.DatasetUtils.{shuffleDataset, splitDataset, splitDatasetWithGroups}
import org.scalactic.{Equality, TolerantNumerics}
import org.scalatest.{FlatSpec, Matchers}

import scala.util.Random

class DatasetUtilsTest extends FlatSpec with Matchers with TestingUtils {

  implicit val rand: Random = new Random(0)
  implicit val doubleTolerance: Equality[Double] = TolerantNumerics.tolerantDoubleEquality(1.0)

  val (x, y) = loadIrisDataset

  "Dataset utils" should "shuffle the dataset correctly" in {
    val (_, yShuffled) = shuffleDataset(x, y)
    breezeEqual(y, yShuffled) shouldBe false
  }

  they should "split the dataset correctly" in {
    val split = splitDataset(x, y)
    split.yTr.length shouldBe 75
    split.yTe.length shouldBe 75
  }

  they should "split the dataset with groups correctly" in {
    val groups = DenseVector((0 until x.rows).map(x => x % 4):_*)
    val split = splitDatasetWithGroups(x, y, groups, proportionTrain = 0.8)
    val groupsTe = split.groupsTe.toArray
    split.groupsTr.forall(trGroup => !groupsTe.contains(trGroup)) shouldBe true
  }
}
