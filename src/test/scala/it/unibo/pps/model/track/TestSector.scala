package it.unibo.pps.model.track

import it.unibo.pps.model.loader.TrackLoader
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class TestSector extends AnyFlatSpec with Matchers:

  "Straight phase" should "return the right action considering the actual position" in {
    val track = TrackLoader("/prolog/basetrack.pl").load
    val sector1 = track.sectors.head
    sector1.phase((150, 150)) shouldBe Phase.Acceleration
    sector1.phase((550, 150)) shouldBe Phase.Deceleration
    sector1.phase((725, 150)) shouldBe Phase.Ended
  }

  "Turn phase" should "return the right action considering the actual position" in {
    val track = TrackLoader("/prolog/basetrack.pl").load
    val sector1 = track.sectors.last
    sector1.phase((150, 150)) shouldBe Phase.Acceleration
    sector1.phase((182, 150)) shouldBe Phase.Ended
  }
