package com.picnicml.doddlemodel.modelselection

import breeze.linalg.{DenseMatrix, DenseVector, convert}
import com.picnicml.doddlemodel.data.Target
import org.scalatest.{FlatSpec, Matchers}

class CrossValidationTest extends FlatSpec with Matchers {

  val cv = CrossValidation(metric = (_: Target, _: Target) => 0, folds = 3)

  private def dummyData(nRows: Int) =
    (DenseMatrix.zeros[Double](nRows, 1), convert(DenseVector((1 to nRows).toArray), Double))

  "3-fold cross validation" should "split 8 examples correctly" in {
    val (x, y) = dummyData(8)
    val splits = cv.splitData(x, y)

    splits.length shouldBe 3
    splits.head.yTr.toArray shouldBe (4 to 8).toArray
    splits.head.yTe.toArray shouldBe (1 to 3).toArray
    splits(1).yTr.toArray shouldBe Array(1, 2, 3, 7, 8)
    splits(1).yTe.toArray shouldBe (4 to 6).toArray
    splits.last.yTr.toArray shouldBe (1 to 6).toArray
    splits.last.yTe.toArray shouldBe Array(7, 8)
  }

  it should "split 9 examples correctly" in {
    val (x, y) = dummyData(9)
    val splits = cv.splitData(x, y)

    splits.length shouldBe 3
    splits.head.yTr.toArray shouldBe (4 to 9).toArray
    splits.head.yTe.toArray shouldBe (1 to 3).toArray
    splits(1).yTr.toArray shouldBe Array(1, 2, 3, 7, 8, 9)
    splits(1).yTe.toArray shouldBe (4 to 6).toArray
    splits.last.yTr.toArray shouldBe (1 to 6).toArray
    splits.last.yTe.toArray shouldBe (7 to 9).toArray
  }

  it should "split 10 examples correctly" in {
    val (x, y) = dummyData(10)
    val splits = cv.splitData(x, y)

    splits.length shouldBe 3
    splits.head.yTr.toArray shouldBe (5 to 10).toArray
    splits.head.yTe.toArray shouldBe (1 to 4).toArray
    splits(1).yTr.toArray shouldBe Array(1, 2, 3, 4, 8, 9, 10)
    splits(1).yTe.toArray shouldBe (5 to 7).toArray
    splits.last.yTr.toArray shouldBe (1 to 7).toArray
    splits.last.yTe.toArray shouldBe (8 to 10).toArray
  }
}
