package it.unibo.pps.engine

import scala.collection.mutable.HashMap

trait TurnAngles:

  def reset(car: String): Unit

  def setAngle(teta: Double, car: String): Unit

  def difference(car: String): Double

object TurnAngles:
  def apply(): TurnAngles = new TurnAnglesImpl()

  private class TurnAnglesImpl() extends TurnAngles:

    private val angles =
      HashMap(("Ferrari", (0.0, 0.0)), ("Mercedes", (0.0, 0.0)), ("Red Bull", (0.0, 0.0)), ("McLaren", (0.0, 0.0)))

    override def reset(car: String): Unit = angles(car) = (0.0, 0.0)

    override def setAngle(teta: Double, car: String): Unit = angles(car) = (angles(car)._2, teta)

    override def difference(car: String): Double = angles(car)._2 - angles(car)._1
