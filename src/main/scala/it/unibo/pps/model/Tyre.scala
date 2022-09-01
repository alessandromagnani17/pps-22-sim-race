package it.unibo.pps.model

import it.unibo.pps.model.Tyre.{HARD, MEDIUM, SOFT}

given Conversion[Tyre, Int] = _ match
  case HARD => 10
  case MEDIUM => 5
  case SOFT => 1

enum Tyre:
  case HARD
  case MEDIUM
  case SOFT

object Tyre:

  def degradation(tyreType: Tyre, distance: Double, velocity: Double, lap: Int): Double =
    (distance + velocity + lap) / (3000 + 50 * tyreType)
