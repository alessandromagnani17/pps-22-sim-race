package it.unibo.pps.view.main_panel

import monix.eval.Task

import javax.swing.{JButton, JLabel}

/** Represents the components of the standings in the starting positions panel
  * @param position
  *   The actual starting grid position
  * @param name
  *   The name of the car
  * @param miniature
  *   The image of the car
  * @param upButton
  *   The up button
  * @param downButton
  *   The down button
  */
case class StartingPositionsComponents(
    val position: Task[JLabel],
    val name: Task[JLabel],
    val miniature: Task[JLabel],
    val upButton: Task[JButton],
    val downButton: Task[JButton]
)
