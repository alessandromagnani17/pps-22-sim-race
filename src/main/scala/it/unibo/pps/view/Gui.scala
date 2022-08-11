package it.unibo.pps.view

import it.unibo.pps.controller.ControllerModule
import monix.eval.Task

import java.awt.{Color, Component, Toolkit}
import javax.swing.{JFrame, JTable, SwingUtilities, WindowConstants}
import monix.execution.Scheduler.Implicits.global
import it.unibo.pps.model.{Car, Driver, Standing, Track, Tyre}

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

  def displaySimulationPanel(track: Track): Unit = SwingUtilities.invokeLater { () =>
    val s = Standing(
      List(
        Car("Ferrari", Tyre.SOFT, Driver(0, 0), 200, DrawingCarParams((100, 100), Color.CYAN)),
        Car("Mercedes", Tyre.SOFT, Driver(0, 0), 200, DrawingCarParams((100, 200), Color.RED)),
        Car("Red Bull", Tyre.SOFT, Driver(0, 0), 200, DrawingCarParams((100, 300), Color.BLUE)),
        Car("McLaren", Tyre.SOFT, Driver(0, 0), 200, DrawingCarParams((100, 400), Color.GREEN))
      )
    )
    val p = for
      fr <- frame
      _ <- simulationPanel.updateStanding(s)
      _ <- simulationPanel.renderTrack(track)
      _ <- fr.getContentPane().removeAll()
      _ <- fr.getContentPane().add(simulationPanel)
      _ <- fr.revalidate()
    yield ()
    p.runSyncUnsafe()
  }
  
  def updateCars(cars: List[Car]): Unit =
    simulationPanel.render(cars)

