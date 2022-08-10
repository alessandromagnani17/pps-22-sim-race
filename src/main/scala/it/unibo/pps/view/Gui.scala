package it.unibo.pps.view

import it.unibo.pps.controller.ControllerModule
import it.unibo.pps.model.{Car, Driver, Tyre}
import monix.eval.Task

import java.awt.{Component, Toolkit}
import javax.swing.{JFrame, JTable, SwingUtilities, WindowConstants}
import monix.execution.Scheduler.Implicits.global

class Gui(width: Int, height: Int, controller: ControllerModule.Controller):

  import it.unibo.pps.utility.GivenConversion.GuiConversion.given

  private val mainPanel = MainPanel(width, height, controller)
  private val simulationPanel = SimulationPanel(width, height, controller)
  private val frame = createFrame()
  controller.createCars()
  //private val cars = createCarsList()


  /*private def createCarsList(): List[Task[Car]] =
    val cars = for
      index <- 0 until numCars
      car = createCar(index)
    yield car
    cars.toList

  private def createCar(index: Int): Task[Car] =
    for
      car <- Car(carNames(index), Tyre.HARD, Driver(1,1), 200)
    yield car*/

  private val p =
    for
      fr <- frame
      _ <- fr.getContentPane().add(mainPanel)
      _ <- fr.setVisible(true)
    yield ()
  p.runSyncUnsafe()

  private def createFrame(): Task[JFrame] =
    for
      fr <- new JFrame("Prova")
      _ <- fr.setSize(width, height)
      _ <- fr.setLocationRelativeTo(null)
      _ <- fr.setResizable(false)
      _ <- fr.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)
    yield fr

  def updateParametersPanel(): Unit = mainPanel.updateParametersPanel()

  def updateDisplayedCar(carIndex: Int, tyresType: String): Unit = mainPanel.updateDisplayedCar(carIndex, tyresType)

  def displaySimulationPanel(): Unit = SwingUtilities.invokeLater { () =>
    val p = for
      fr <- frame
      _ <- fr.getContentPane().removeAll()
      _ <- fr.getContentPane().add(simulationPanel)
      _ <- fr.revalidate()
    yield ()
    p.runSyncUnsafe()
  }


