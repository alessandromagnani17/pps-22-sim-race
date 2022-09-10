package it.unibo.pps.model

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class TestFuel extends AnyFlatSpec with Matchers:

  "Consumed fuel in a null movement " should "be zero" in {
    Car.decreaseFuel(0) shouldBe 0
  }

  "Fuel" should "always decrease" in {
    var fuel: List[Double] = List(100)
    for i <- 1 to 30 do fuel = fuel :+ Car.decreaseFuel(10)
    fuel.reverse shouldBe sorted
  }
