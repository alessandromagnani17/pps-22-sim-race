package it.unibo.pps.utility

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import it.unibo.pps.utility.PimpScala.RichTuple2.*

class TestPimpScala extends AnyFlatSpec with Matchers:

  "Euclidean distance of two horizontally aligned points" should "be the difference of the x" in {
    (0, 0) euclideanDistance (10, 0) shouldBe 10
  }

  "Euclidean distance of two vertically aligned points" should "be the difference of the y" in {
    (0, 0) euclideanDistance (0, 10) shouldBe 10
  }

  "Euclidean distance of two overlapping points" should "be zero" in {
    (54, 54) euclideanDistance (54, 54) shouldBe 0
  }
