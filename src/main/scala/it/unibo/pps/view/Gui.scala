package it.unibo.pps.view

import it.unibo.pps.controller.ControllerModule
import monix.eval.Task
import java.awt.{Component, Toolkit}
import javax.swing.{JFrame, JTable, WindowConstants}
import monix.execution.Scheduler.Implicits.global

/*=======
import java.awt.Component
import javax.swing.JFrame
import monix.execution.Scheduler.Implicits.global
import javax.swing.WindowConstants

>>>>>>> ee4390504ff3ef444b5016cc614bdc51aa8fcb7f*/
class Gui(width: Int, height: Int, controller: ControllerModule.Controller):
  given Conversion[Unit, Task[Unit]] = Task(_)
  given Conversion[Component, Task[Component]] = Task(_)
  given Conversion[JFrame, Task[JFrame]] = Task(_)

  private val simulationPanel = SimulationPanel(width, height, controller)
  /*=======
  private val initialPanel = MainPanel(width, height, controller)
>>>>>>> ee4390504ff3ef444b5016cc614bdc51aa8fcb7f*/
  private val frame = createFrame()

  private val p =
    for
      fr <- frame
      _ <- fr.add(simulationPanel)
      /*=======
      _ <- fr.add(initialPanel)
>>>>>>> ee4390504ff3ef444b5016cc614bdc51aa8fcb7f*/
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

  def changeCar(carIndex: Int, tyresType: String): Unit = ???

object Prova extends App:
  new Gui(1296, 810, null)
/*
=======
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
>>>>>>> ee4390504ff3ef444b5016cc614bdc51aa8fcb7f*/
