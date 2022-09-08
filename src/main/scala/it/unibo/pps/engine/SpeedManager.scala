package it.unibo.pps.engine

import it.unibo.pps.engine.SimulationConstants.*

trait SpeedManager:

  /** @return the actual simulation speed */
  def speed: Double

  /** Method that decreases the actual simulation speed
    *
    * If the speed is already at the minimum value it does nothing
    */
  def decreaseSpeed: Unit

  /** Method that increases the actual simulation speed
    *
    * If the speed is already at the maximum value it does nothing
    */
  def increaseSpeed: Unit

object SpeedManager:
  def apply(): SpeedManager = new SpeedManagerImpl()

  private class SpeedManagerImpl() extends SpeedManager:

    var _speed: Double = DEFAULT_SPEED

    override def speed: Double = _speed

    override def decreaseSpeed: Unit = _speed match
      case HIGH_SPEED => _speed = DEFAULT_SPEED
      case DEFAULT_SPEED => _speed = LOW_SPEED
      case _ =>

    override def increaseSpeed: Unit = _speed match
      case LOW_SPEED => _speed = DEFAULT_SPEED
      case DEFAULT_SPEED => _speed = HIGH_SPEED
      case _ =>
