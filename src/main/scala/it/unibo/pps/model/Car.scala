package it.unibo.pps.model

import javax.swing.Icon
import it.unibo.pps.view.simulation_panel.RenderCarParams

case class Car(
    var path: String,
    name: String,
    var tyre: Tyre,
    driver: Driver,
    var maxSpeed: Int,
    var actualLap: Int,
    var actualSpeed: Int,
    var acceleration: Double,
    var actualSector: Sector,
    var raceTime: Int,
    var lapTime: Int,
    var fastestLap: Int,
    val fuel: Double,
    val degradation: Double,
    var renderCarParams: RenderCarParams
)
