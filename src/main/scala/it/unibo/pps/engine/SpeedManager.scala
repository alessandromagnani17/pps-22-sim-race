package it.unibo.pps.engine

import it.unibo.pps.engine.SimulationConstants.*

trait SpeedManager:

  /** @return the actual simulation speed */
  def _simulationSpeed: Double

  /** Method that decreases the actual simulation speed
    *
    * If the speed is already at the minimum value it does nothing
    */
  def decreaseSpeed(): Unit

  /** Method that increases the actual simulation speed
    *
    * If the speed is already at the maximum value it does nothing
    */
  def increaseSpeed(): Unit

object SpeedManager:
  def apply(): SpeedManager = new SpeedManagerImpl()

  private class SpeedManagerImpl() extends SpeedManager:

    var speed: Double = DEFAULT_SPEED

    override def _simulationSpeed: Double = speed

    override def decreaseSpeed(): Unit = speed match {
      case HIGH_SPEED => speed = DEFAULT_SPEED
      case DEFAULT_SPEED => speed = LOW_SPEED
      case _ =>
    }

    override def increaseSpeed(): Unit = speed match {
      case LOW_SPEED => speed = DEFAULT_SPEED
      case DEFAULT_SPEED => speed = HIGH_SPEED
      case _ =>
    }
