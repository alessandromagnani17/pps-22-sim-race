package it.unibo.pps.view

import it.unibo.pps.controller.ControllerModule
import monix.eval.Task

import java.awt.{Component, Toolkit}
import javax.swing.JFrame
import monix.execution.Scheduler.Implicits.global

import javax.swing.WindowConstants

class Gui(width: Int, height: Int, controller: ControllerModule.Controller):
  given Conversion[Unit, Task[Unit]] = Task(_)
  given Conversion[Component, Task[Component]] = Task(_)
  given Conversion[JFrame, Task[JFrame]] = Task(_)

  private val simulationPanel = SimulationPanel(width, height, controller)
  private val frame = createFrame()

  private val p =
    for
      fr <- frame
      _ <- fr.add(simulationPanel)
      _ <- fr.setVisible(true)
    yield ()
  p.runAsyncAndForget

  private def createFrame(): Task[JFrame] =
    for
      fr <- new JFrame("Prova")
      _ <- fr.setSize(width, height)
      _ <- fr.setLocationRelativeTo(null)
      _ <- fr.setResizable(false)
      _ <- fr.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)
    yield fr

object Prova extends App:
  val screenSize = Toolkit.getDefaultToolkit.getScreenSize
  val w = (screenSize.width * 0.9).toInt
  val h = (screenSize.height * 0.9).toInt
  new Gui(w, h, null)
