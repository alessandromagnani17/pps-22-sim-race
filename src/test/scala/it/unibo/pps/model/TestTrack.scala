package it.unibo.pps.model

import it.unibo.pps.view.simulation_panel.DrawingStraightParams
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

  "With track Builder you" should "create a base track" in {
    val track = TrackBuilder().createBaseTrack()
    track.getSectors().size shouldBe 4
    track
      .getSectors()
      .filter(s =>
        s match {
          case straight: Sector.Straight => true
          case _ => false
        }
      )
      .size shouldBe 2
    track
      .getSectors()
      .filter(s =>
        s match {
          case turn: Sector.Turn => true
          case _ => false
        }
      )
      .size shouldBe 2
  }
