package it.unibo.pps.view.simulation_panel

import monix.eval.Task

import javax.swing.JLabel

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
