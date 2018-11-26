package com.picnicml.doddlemodel.syntax

import com.picnicml.doddlemodel.TestingUtils
import com.picnicml.doddlemodel.linear.{LinearRegression, LogisticRegression}
import com.picnicml.doddlemodel.preprocessing.StandardScaler
import org.scalatest.{FlatSpec, Matchers}

class SyntaxTest extends FlatSpec with Matchers with TestingUtils {

  val (x, y) = dummyData(2)

  "Syntax" should "provide syntax for predictor typeclass instances" in {
    import com.picnicml.doddlemodel.syntax.PredictorSyntax._
    val model = LogisticRegression()
    val trainedModel = model.fit(x, y)
    trainedModel.predict(x)
    trainedModel.isFitted
  }

  it should "provide syntax for classifier typeclass instances" in {
    import com.picnicml.doddlemodel.syntax.ClassifierSyntax._
    val model = LogisticRegression()
    val trainedModel = model.fit(x, y)
    trainedModel.predict(x)
    trainedModel.predictProba(x)
    trainedModel.isFitted
    trainedModel.numClasses
  }

  it should "provide syntax for regressor typeclass instances" in {
    import com.picnicml.doddlemodel.syntax.RegressorSyntax._
    val model = LinearRegression()
    val trainedModel = model.fit(x, y)
    trainedModel.predict(x)
    trainedModel.isFitted
  }

  it should "provide syntax for transformer typeclass instances" in {
    import com.picnicml.doddlemodel.syntax.TransformerSyntax._
    val transformer = StandardScaler()
    val trainedTransformer = transformer.fit(x)
    trainedTransformer.transform(x)
    trainedTransformer.isFitted
  }
}
