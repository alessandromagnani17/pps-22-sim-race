package it.unibo.pps.model

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import it.unibo.pps.model.{Straight, Turn}
import it.unibo.pps.model.loader.TrackLoader

class TestTrack extends AnyFlatSpec with Matchers:

  "An empty track" should "return an empty List" in {
    val t = Track()
    t.sectors shouldBe List.empty
  }

  "After adding a sector the track" should "be non-empty" in {
    val t = Track()
    val sector = Straight(1, Direction.Forward, RenderStraightParams((0, 0), (0, 0), (0, 0), (0, 0), 0))
    t.addSector(sector)
    assert(t.sectors.size > 0)
  }


  /*"With track Builder you" should "create a base track" in {
    val track = TrackBuilder().createBaseTrack()
=======
  "With track loader you" should "create a base track" in {
    val track = TrackLoader("/prolog/basetrack.pl").load
>>>>>>> 2f6051a278c2660274cc8de109d014b9265a7d66
    track.sectors.size shouldBe 4
    track.sectors
      .filter(s =>
        s match {
          case straight: Straight => true
          case _ => false
        }
      )
      .size shouldBe 2
    track.sectors
      .filter(s =>
        s match {
          case turn: Turn => true
          case _ => false
        }
      )
      .size shouldBe 2
<<<<<<< HEAD
  }
=======
  }

  "Next sector" should "return the next sector" in {
    val track = TrackLoader("/prolog/basetrack.pl").load
    val sector = Straight(1, Direction.Forward, RenderStraightParams((0, 0), (0, 0), (0, 0), (0, 0), 0))
    val nextSector = track.nextSector(sector)
    nextSector.id shouldBe sector.id + 1
  }

  "Next sector" should "also work cyclically" in {
    val track = TrackLoader("/prolog/basetrack.pl").load
    val sector = Straight(4, Direction.Forward, RenderStraightParams((0, 0), (0, 0), (0, 0), (0, 0), 0))
    val nextSector = track.nextSector(sector)
    nextSector.id shouldBe 1
  }
>>>>>>> 2f6051a278c2660274cc8de109d014b9265a7d66*/
