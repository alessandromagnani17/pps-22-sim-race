package it.unibo.pps.model.car

given Conversion[Tyre, Double] = _ match
  case Tyre.HARD => 4.0
  case Tyre.MEDIUM => 3.0
  case Tyre.SOFT => 2.0

enum Tyre:
  case HARD
  case MEDIUM
  case SOFT

object Tyre:

  /** Computes the tyre degradation
    * @param tyreType
    *   Type of the tyre
    * @param lap
    *   Current lap
    */
  def degradation(tyreType: Tyre, lap: Double): Double = Math.exp((-1 / tyreType) * (lap / 10))
