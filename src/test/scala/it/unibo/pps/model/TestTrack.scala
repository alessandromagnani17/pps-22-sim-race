package it.unibo.pps.model

import it.unibo.pps.view.DrawingStraightParams
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class TestTrack extends AnyFlatSpec with Matchers:

  "An empty track" should "return an empty List" in {
    val t = Track()
    t._sectors() shouldBe List.empty
  }

  "After adding a sector the track" should "be non-empty" in {
    val t = Track()
    val sector = Sector.Straight(1, DrawingStraightParams((0, 0), (0, 0), (0, 0), (0, 0)))
    t.addSector(sector)
    assert(t._sectors().size > 0)
  }

  "With track Builder you" should "create a base track" in {
    val track = TrackBuilder().createBaseTrack()
    track._sectors().size shouldBe 4
    track
      ._sectors()
      .filter(s =>
        s match {
          case straight: Sector.Straight => true
          case _ => false
        }
      )
      .size shouldBe 2
    track
      ._sectors()
      .filter(s =>
        s match {
          case turn: Sector.Turn => true
          case _ => false
        }
      )
      .size shouldBe 2
  }
