package it.unibo.pps.model.factor

import it.unibo.pps.model.Tyre
given Conversion[Double, Int] = _.toInt

/** Represents a generic factor that may affect car velocity */
sealed trait Factor:
  type E

  /** Computes the damage that affect car velocity */
  def damage(velocity: Int, parameter: E): Int

/** Represents the speed limitation derived from fuel weight */
class FuelFactor extends Factor:
  override type E = Double
  override def damage(velocity: Int, fuel: E): Int = ((fuel / 120.0) / 20.0) * velocity

/** Represents the speed limitation derived from tyre type */
class TyreFactor extends Factor:
  override type E = (Tyre, Int)
  override def damage(velocity: Int, p: E): Int = p._1 match
    case Tyre.SOFT => (velocity / 5.0) * (Math.exp((1.0 / 5.0) * (p._2 / 10.0)) - 0.9)
    case Tyre.MEDIUM => (velocity / 5.0) * 0.25
    case Tyre.HARD => (velocity / 5.0) * (Math.exp((-1.0 / 3.0) * (p._2 / 10.0)) - 0.25)

/** Represents the speed limitation derived from tyre degradation */
class DegradationFactor extends Factor:
  override type E = Double
  override def damage(velocity: Int, degradation: E): Int = ((1 - degradation) / 10.0) * velocity

/** Represents the sum of all the factors */
object CarFactorsManager:

  private val fuelFactor = FuelFactor()
  private val tyreFactor = TyreFactor()
  private val degradationFactor = DegradationFactor()

  def totalDamage(velocity: Int, fuel: Double, t: (Tyre, Int), degradation: Double): Int =
    fuelFactor.damage(velocity, fuel)
      + tyreFactor.damage(velocity, t)
      + degradationFactor.damage(velocity, degradation)
