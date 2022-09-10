package it.unibo.pps.view.main_panel

import monix.eval.Task

import javax.swing.{JButton, JLabel}

case class StartingPositionsComponents (
    val position: Task[JLabel],
    val name: Task[JLabel],
    val miniature: Task[JLabel],
    val upButton: Task[JButton],
    val downButton: Task[JButton]
)
