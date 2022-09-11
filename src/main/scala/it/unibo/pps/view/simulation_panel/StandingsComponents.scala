package it.unibo.pps.view.simulation_panel

import monix.eval.Task

import javax.swing.JLabel
/** Represents the components of the real-time standings
  * @param position
  *   The actual car position
  * @param name
  *   The name of the car
  * @param color
  *   The car color
  * @param miniature
  *   The image of the car
  * @param tyres
  *   The car tyres
  * @param raceTime
  *   The car race time
  * @param lapTime
  *   The car lap time
  * @param fastestLap
  *   The car fastest lap
  * @param fastestLapIcon
  *   The fastest lap logo
  */
case class StandingsComponents(
    position: Task[JLabel],
    name: Task[JLabel],
    color: Task[JLabel],
    miniature: Task[JLabel],
    tyres: Task[JLabel],
    raceTime: Task[JLabel],
    lapTime: Task[JLabel],
    fastestLap: Task[JLabel],
    fastestLapIcon: Task[JLabel]
)
