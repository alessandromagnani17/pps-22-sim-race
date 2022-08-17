package it.unibo.pps.model

import javax.swing.Icon
import it.unibo.pps.view.simulation_panel.DrawingCarParams

case class Car(var path: String, name: String, var tyre: Tyre, driver: Driver, var maxSpeed: Int, drawingCarParams: DrawingCarParams)
