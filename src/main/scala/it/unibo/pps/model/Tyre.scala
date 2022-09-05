package it.unibo.pps.model

import it.unibo.pps.model.Tyre.{HARD, MEDIUM, SOFT}

given Conversion[Tyre, Double] = _ match
  case HARD => 2.0
  case MEDIUM => 3.0
  case SOFT => 4.0

enum Tyre:
  case HARD
  case MEDIUM
  case SOFT

object Tyre:
  def degradation(tyreType: Tyre, lap: Double): Double = Math.exp((-1 / tyreType) * (lap / 10))
