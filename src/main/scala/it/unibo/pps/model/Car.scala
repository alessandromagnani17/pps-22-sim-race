package it.unibo.pps.model

import javax.swing.Icon
import it.unibo.pps.view.simulation_panel.DrawingCarParams

case class Car(
    var path: String,
    name: String,
    var tyre: Tyre,
    driver: Driver,
    var maxSpeed: Int,
    var actualLap: Int,
    var actualSpeed: Double,
    var acceleration: Double,
    var actualSector: Sector,
    var radius: Int,
    var fuel: Double,
    var drawingCarParams: DrawingCarParams
)

/*
object Car:
  def computeNewVelocity(acceleration: Double , body: Car, t: Double): Double = body.velocity + acceleration * t

  def computeNewPosition(acceleration: Double , body: Car, t: Double): Tuple2[Double, Double] =
    (body.drawingCarParams.position._1 + body.velocity * t + 0.5 * acceleration * (t*t), body.drawingCarParams.position._2)

  def dummyNewPosition(oldPosition: Tuple2[Double, Double]) = (oldPosition._1 + 2, oldPosition._2)
 */
