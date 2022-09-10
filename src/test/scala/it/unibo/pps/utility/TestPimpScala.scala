package it.unibo.pps.utility

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import it.unibo.pps.utility.PimpScala.RichTuple2.*

class TestPimpScala extends AnyFlatSpec with Matchers:

  "test euclidean distance" should "work" in {
    (0, 0) euclideanDistance (10, 0) shouldBe 10
  }

  "test euclidean distance" should "work2" in {
    (0, 0) euclideanDistance (0, 0) shouldBe 0
  }

  "test euclidean distance" should "work4" in {
    (1, 2) euclideanDistance (17, 54) shouldBe 54
  }
