package it.unibo.pps.view

import it.unibo.pps.controller.ControllerModule
import monix.eval.Task

import java.awt.{Component, Toolkit}
import javax.swing.{JFrame, JTable, SwingUtilities, WindowConstants}
import monix.execution.Scheduler.Implicits.global
import it.unibo.pps.model.{Standing, Car, Tyre, Driver}

class Gui(width: Int, height: Int, controller: ControllerModule.Controller):

  import it.unibo.pps.utility.GivenConversion.GuiConversion.given

  private val initialPanel = MainPanel(width, height, controller)
  private val simulationPanel = SimulationPanel(width, height, controller)

  private lazy val frame: Task[JFrame] =
    for
      fr <- new JFrame("Racing Simulator")
      _ <- fr.setSize(width, height)
      _ <- fr.setLocationRelativeTo(null)
      _ <- fr.setResizable(false)
      _ <- fr.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)
    yield fr

  private lazy val p =
    for
      fr <- frame
      _ <- fr.getContentPane().add(initialPanel)
      _ <- fr.setVisible(true)
    yield ()
  p.runSyncUnsafe()

  def updateDisplayedCar(carIndex: Int, tyresType: String): Unit = initialPanel.updateDisplayedCar(carIndex, tyresType)

  def displaySimulationPanel(): Unit = SwingUtilities.invokeLater { () =>
    val s = Standing(
      List(
        Car("Ferrari", Tyre.SOFT, Driver(0, 0), 200),
        Car("Mercedes", Tyre.SOFT, Driver(0, 0), 200),
        Car("Red Bull", Tyre.SOFT, Driver(0, 0), 200),
        Car("McLaren", Tyre.SOFT, Driver(0, 0), 200)
      )
    )
    val p = for
      fr <- frame
      _ <- simulationPanel.updateStanding(s)
      _ <- fr.getContentPane().removeAll()
      _ <- fr.getContentPane().add(simulationPanel)
      _ <- fr.revalidate()
    yield ()
    p.runSyncUnsafe()
  }
