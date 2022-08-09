package it.unibo.pps.engine

import it.unibo.pps.engine.SimulationConstants.*

trait SpeedManager:
  def _simulationSpeed: Double
  def decreaseSpeed(): Unit
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
