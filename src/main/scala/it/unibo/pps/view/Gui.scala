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

  def _simulationPanel = simulationPanel

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

  def displaySimulationPanel(track: Track, standing: Standing): Unit = SwingUtilities.invokeLater { () =>
    val p = for
      fr <- frame
      _ <- simulationPanel.updateStanding(standing)
      _ <- simulationPanel.renderTrack(track)
      _ <- fr.getContentPane().removeAll()
      _ <- fr.getContentPane().add(simulationPanel)
      _ <- fr.revalidate()
    yield ()
    p.runSyncUnsafe()
  }
