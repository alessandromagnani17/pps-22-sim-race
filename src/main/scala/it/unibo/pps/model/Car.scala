package it.unibo.pps.model

import javax.swing.Icon

/**
 * Case class that represents a car
 * @param path
 *  The car's image path
 * @param name
 *  The car's name
 * @param tyre
 *  The car's tyres type
 * @param driver
 *  The car's driver
 * @param maxSpeed
 *  The car's maximum speed
 * @param actualLap
 *  The car's actual lap
 * @param actualSpeed
 *  The car's actual speed
 * @param acceleration
 *  The car's acceleration
 * @param actualSector
 *  The car's actual sector
 * @param raceTime
 *  The car's race time
 * @param lapTime
 *  The car's lap time
 * @param fastestLap
 *  The car's fastest lap
 * @param fuel
 *  The car's fuel
 * @param degradation
 *  The car's degradation
 * @param renderCarParams
 *  The car's render parameters
 */
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
