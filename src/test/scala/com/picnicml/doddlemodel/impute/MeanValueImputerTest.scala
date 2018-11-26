package com.picnicml.doddlemodel.impute

import breeze.linalg.{DenseMatrix, DenseVector}
import com.picnicml.doddlemodel.TestingUtils
import com.picnicml.doddlemodel.impute.MeanValueImputer.ev
import org.scalatest.{FlatSpec, Matchers, OptionValues}

class MeanValueImputerTest extends FlatSpec with Matchers with TestingUtils with OptionValues {

  "Mean value imputer" should "impute data correctly" in {
    val xMissing = DenseMatrix(
      List(Double.NaN, 1.0, 2.0),
      List(3.0, Double.NaN, 5.0),
      List(6.0, 7.0, 8.0)
    )

    val xImputedExpected = DenseMatrix(
      List(4.5, 1.0, 2.0),
      List(3.0, 4.0, 5.0),
      List(6.0, 7.0, 8.0)
    )

    val imputer = MeanValueImputer()
    val fittedImputer = ev.fit(imputer, xMissing)

    breezeEqual(fittedImputer.means.value, DenseVector(4.5, 4.0, 5.0)) shouldBe true
    breezeEqual(ev.transform(fittedImputer, xMissing), xImputedExpected)
  }
}
