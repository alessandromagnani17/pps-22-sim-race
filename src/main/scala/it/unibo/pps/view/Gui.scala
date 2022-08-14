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
  private val _simulationPanel = SimulationPanel(width, height, controller)

  def simulationPanel = _simulationPanel

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
      _ <- _simulationPanel.updateStanding(standing)
      _ <- _simulationPanel.renderTrack(track)
      _ <- fr.getContentPane().removeAll()
      _ <- fr.getContentPane().add(_simulationPanel)
      _ <- fr.revalidate()
    yield ()
    p.runSyncUnsafe()
  }
