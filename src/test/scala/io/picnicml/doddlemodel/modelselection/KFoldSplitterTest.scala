package io.picnicml.doddlemodel.modelselection

import io.picnicml.doddlemodel.TestingUtils
import org.scalatest.{FlatSpec, Matchers}

import scala.util.Random

class KFoldSplitterTest extends FlatSpec with Matchers with TestingUtils {

  val splitter = KFoldSplitter(numFolds = 3, shuffleRows = false)

  "KFoldSplitter" should "split 8 examples" in {
    implicit val rand: Random = new Random()
    val (x, y) = dummyData(8)
    val splits = splitter.splitData(x, y)

    splits.length shouldBe 3
    splits(0).yTr.toArray shouldBe (3 to 7).toArray
    splits(0).yTe.toArray shouldBe (0 to 2).toArray
    splits(1).yTr.toArray shouldBe Array(0, 1, 2, 6, 7)
    splits(1).yTe.toArray shouldBe (3 to 5).toArray
    splits(2).yTr.toArray shouldBe (0 to 5).toArray
    splits(2).yTe.toArray shouldBe Array(6, 7)
  }

  it should "split 9 examples" in {
    implicit val rand: Random = new Random()
    val (x, y) = dummyData(9)
    val splits = splitter.splitData(x, y)

    splits.length shouldBe 3
    splits(0).yTr.toArray shouldBe (3 to 8).toArray
    splits(0).yTe.toArray shouldBe (0 to 2).toArray
    splits(1).yTr.toArray shouldBe Array(0, 1, 2, 6, 7, 8)
    splits(1).yTe.toArray shouldBe (3 to 5).toArray
    splits(2).yTr.toArray shouldBe (0 to 5).toArray
    splits(2).yTe.toArray shouldBe (6 to 8).toArray
  }

  it should "split 10 examples" in {
    implicit val rand: Random = new Random()
    val (x, y) = dummyData(10)
    val splits = splitter.splitData(x, y)

    splits.length shouldBe 3
    splits(0).yTr.toArray shouldBe (4 to 9).toArray
    splits(0).yTe.toArray shouldBe (0 to 3).toArray
    splits(1).yTr.toArray shouldBe Array(0, 1, 2, 3, 7, 8, 9)
    splits(1).yTe.toArray shouldBe (4 to 6).toArray
    splits(2).yTr.toArray shouldBe (0 to 6).toArray
    splits(2).yTe.toArray shouldBe (7 to 9).toArray
  }
}
