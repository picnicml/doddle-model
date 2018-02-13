package com.doddle

import org.scalatest.{FlatSpec, Matchers}

class BaseTest extends FlatSpec with Matchers {

  "Base" should "base" in {
    val base = new Base()
    base.base should be(true)
  }
}
