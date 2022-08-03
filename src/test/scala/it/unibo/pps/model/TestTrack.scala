package it.unibo.pps.model

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class TestTrack extends AnyFlatSpec with Matchers:

  "An empty track" should "return an empty List" in {
    val t = Track()
    t.getSectors() shouldBe List.empty
  }

  /*
  "After adding a sector the track" should "be non-empty" in {
    val t = Track()
    t.addSector(Sector.Straight(1, 0, 0, 10, 10))
    t.addSector(Sector.Straight(2, 0, 0, 15, 15))
    assert(t.getSectors().size > 0)
  }
  */
