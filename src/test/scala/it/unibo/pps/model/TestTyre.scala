package it.unibo.pps.model

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class TestTyre extends AnyFlatSpec with Matchers:

  "Tyre degradation initially" should "be 1" in {
    Tyre.degradation(Tyre.HARD, 0) shouldBe 1
    Tyre.degradation(Tyre.SOFT, 0) shouldBe 1
    Tyre.degradation(Tyre.MEDIUM, 0) shouldBe 1
  }

  "Tyre degradation of SOFT tyre for each lap" should "be lower than HARD tyre" in {
    for lap <- 1 to 30 do
      Tyre.degradation(Tyre.SOFT, lap) should be <= Tyre.degradation(Tyre.HARD, lap)
  }

  "Tyre degradation of SOFT tyre for each lap" should "be lower than MEDIUM tyre" in {
    for lap <- 1 to 30 do
      Tyre.degradation(Tyre.SOFT, lap) should be <= Tyre.degradation(Tyre.MEDIUM, lap)
  }

  "Tyre degradation of MEDIUM tyre for each lap" should "be lower than HARD tyre" in {
    for lap <- 1 to 30 do
      Tyre.degradation(Tyre.MEDIUM, lap) should be <= Tyre.degradation(Tyre.HARD, lap)
  }

