package it.unibo.pps.model.factor

import it.unibo.pps.model.Tyre
given Conversion[Double, Int] = _.toInt

sealed trait Factor:
  type E
  def damage(velocity: Int, parameter: E): Int

class FuelFactor extends Factor:
  override type E = Double
  override def damage(velocity: Int, fuel: E): Int = ((fuel / 120.0) / 20.0) * velocity

class TyreFactor extends Factor:
  override type E = (Tyre, Int)
  override def damage(velocity: Int, tyre: E): Int = tyre._1 match
    case Tyre.SOFT => (velocity / 5.0) * (Math.exp((2.0 / 13.0) * (tyre._2 / 10.0)) - 0.9)
    case Tyre.MEDIUM => (velocity / 5.0) * 0.4
    case Tyre.HARD => (velocity / 5.0) * (Math.exp((-1.0 / 3.0) * (tyre._2 / 10.0)) - 0.25)

class DegradationFactor extends Factor:
  override type E = Double
  override def damage(velocity: Int, degradation: E): Int = ((1 - degradation) / 10.0) * velocity

object CarFactorsManager:

  private val fuelFactor = FuelFactor()
  private val tyreFactor = TyreFactor()
  private val degradationFactor = DegradationFactor()

  def totalDamage(velocity: Int, fuel: Double, t: (Tyre, Int), degradation: Double): Int =
    fuelFactor.damage(velocity, fuel)
      + tyreFactor.damage(velocity, t)
      + degradationFactor.damage(velocity, degradation)
