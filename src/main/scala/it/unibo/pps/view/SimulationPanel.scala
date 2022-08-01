package it.unibo.pps.view

import it.unibo.pps.controller.ControllerModule

import java.awt.{Dimension, Graphics}
import javax.swing.{JButton, JPanel}
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global

trait SimulationPanel extends JPanel:

  /** Method for rendering the new snapshot of the simulation
   *
   */
  def render(): Unit

object SimulationPanel:

  given Conversion[Enviroment, Task[Enviroment]] = Task(_)
  given Conversion[Int, Task[Int]] = Task(_)
  given Conversion[Unit, Task[Unit]] = Task(_)

  def apply(width: Int, height: Int, controller: ControllerModule.Controller): SimulationPanel = new SimulationPanelImpl(width, height, controller)

  private class SimulationPanelImpl(width: Int, height: Int, controller: ControllerModule.Controller) extends SimulationPanel:
    self=>
    private val canvas = createCanvas()
    canvas.foreach(c => self.add(c))


    override def render(): Unit = ???

    private def createCanvas(): Task[Enviroment] =
      val w = (width*0.6).toInt
      val h = (height*0.7).toInt
      for
        cnv <- new Enviroment(w, h)
        _ <- cnv.setSize(w, h)
        _ <- cnv.setVisible(true)
      yield cnv

class Enviroment(val w: Int, val h: Int) extends JPanel:

      override def getPreferredSize: Dimension = new Dimension(w, h)
      override def paintComponent(g: Graphics): Unit = g.drawArc(1, 100, 50, 50, 0, 180)
