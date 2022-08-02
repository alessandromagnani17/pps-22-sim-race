package it.unibo.pps.model

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class TestTrack extends AnyFlatSpec with Matchers:

  "An empty track" should "return an empty Option" in {
    val t = Track()
    t.getSectorByID(1) shouldBe Option.empty
  }
