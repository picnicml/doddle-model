package com.picnicml.doddlemodel.impute

import breeze.linalg.{DenseMatrix, DenseVector}
import com.picnicml.doddlemodel.TestingUtils
import com.picnicml.doddlemodel.syntax.TransformerSyntax._
import org.scalatest.{FlatSpec, Matchers, OptionValues}

class MeanValueImputerTest extends FlatSpec with Matchers with TestingUtils with OptionValues {

  "Mean value imputer" should "impute data correctly" in {
    val x = DenseMatrix(
      List(1.0, 2.0, 3.0),
      List(1.0, 2.0, 3.0),
      List(1.0, 2.0, 3.0)
    )

    val xMissing = x.copy
    xMissing(0, 0) = Double.NaN
    xMissing(1, 1) = Double.NaN

    val imputer = MeanValueImputer()
    val fittedImputer = imputer.fit(xMissing)

    breezeEqual(fittedImputer.means.value, DenseVector(1.0, 2.0, 3.0)) shouldBe true
    breezeEqual(fittedImputer.transform(xMissing), x)
  }
}
