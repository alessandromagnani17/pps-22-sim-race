package it.unibo.pps.view.simulation_panel

import monix.eval.Task

import javax.swing.JLabel

case class StandingsComponent(
    val position: Task[JLabel],
    val name: Task[JLabel],
    val color: Task[JLabel],
    val miniature: Task[JLabel],
    val tyres: Task[JLabel],
    val raceTime: Task[JLabel],
    val lapTime: Task[JLabel],
    val fastestLap: Task[JLabel],
    val fastestLapIcon: Task[JLabel]
)
