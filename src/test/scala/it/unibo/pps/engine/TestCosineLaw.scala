package it.unibo.pps.engine

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class TestCosineLaw extends AnyFlatSpec with Matchers:

  "Angle between two overlapping points" should "be zero" in {
    angleBetweenPoints((10, 10), (10, 10), 5) shouldBe 0
  }

  "Angle between R2 unit vectors" should "be 90 degrees" in {
    angleBetweenPoints((0, 1), (1, 0), 1) shouldBe Math.toRadians(90)
  }
