package it.unibo.pps.view

import it.unibo.pps.controller.ControllerModule
import monix.eval.Task
import java.awt.Component
import javax.swing.JFrame
import monix.execution.Scheduler.Implicits.global
import javax.swing.WindowConstants

class Gui(width: Int, height: Int, controller: ControllerModule.Controller):
  given Conversion[Unit, Task[Unit]] = Task(_)
  given Conversion[Component, Task[Component]] = Task(_)
  given Conversion[JFrame, Task[JFrame]] = Task(_)

  private val initialPanel = MainPanel(width, height, controller)
  private val frame = createFrame()

  private val p =
    for
      fr <- frame
      _ <- fr.add(initialPanel)
      _ <- fr.setVisible(true)
    yield ()
  p.runAsyncAndForget

  private def createFrame(): Task[JFrame] =
    for
      fr <- new JFrame("Sim-race")
      _ <- fr.setSize(width, height)
      _ <- fr.setLocationRelativeTo(null)
      _ <- fr.setResizable(false)
      //_ <- fr.getContentPane().setLayout(null) // AGGIUNTA NUOVA
      _ <- fr.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)
    yield fr
    
  def changeCar(carIndex: Int, tyresType: String): Unit = initialPanel.changeCar(carIndex, tyresType)

object Prova extends App:
  new Gui(1000, 650, null)
