package it.unibo.pps.model

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class TestTrack extends AnyFlatSpec with Matchers:

  "An empty track" should "return an empty Option" in {
    val t = Track()
    t.getSectorByID(1) shouldBe Option.empty
  }

  "After adding a sector you" should "be able to retrieve it" in {
    val t = Track()
    val id = 1
    t.addSector(Straight(id, 0, 0, 10, 10))
    val s = t.getSectorByID(id)
    assert(s.get._id == id)
  }
