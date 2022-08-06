package it.unibo.pps.model

import it.unibo.pps.view.DrawingStraightParams
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class TestTrack extends AnyFlatSpec with Matchers:

  "An empty track" should "return an empty List" in {
    val t = Track()
    t.getSectors() shouldBe List.empty
  }

  "After adding a sector the track" should "be non-empty" in {
    val t = Track()
    val sector = Sector.Straight(1, DrawingStraightParams((0, 0), (0, 0), (0, 0), (0, 0)))
    t.addSector(sector)
    assert(t.getSectors().size > 0)
  }
