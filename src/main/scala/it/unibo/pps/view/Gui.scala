package it.unibo.pps.view

import it.unibo.pps.controller.ControllerModule
import it.unibo.pps.view.main_panel.MainPanel
import it.unibo.pps.view.main_panel.StartingPositionsPanel
import it.unibo.pps.view.simulation_panel.SimulationPanel
import monix.eval.Task

import java.awt.{Color, Component, Toolkit}
import javax.swing.{JFrame, JTable, SwingUtilities, WindowConstants}
import monix.execution.Scheduler.Implicits.global
import it.unibo.pps.model.{Car, Driver, Standing, Track, Tyre}
import it.unibo.pps.view.Constants.GuiConstants.*
import it.unibo.pps.view.end_race_panel.EndRacePanel

class Gui(controller: ControllerModule.Controller):

  import it.unibo.pps.utility.GivenConversion.GuiConversion.given

  private val mainPanel = MainPanel(controller)
  private val _simulationPanel = SimulationPanel(controller)
  private val startingPositionsPanel = StartingPositionsPanel(controller)
  private val frame = createFrame("sim-race", FRAME_WIDTH, FRAME_HEIGHT, WindowConstants.EXIT_ON_CLOSE)
  private val startingPositionsFrame =
    createFrame("starting-positions", STARTING_POS_FRAME_WIDTH, STARTING_POS_FRAME_HEIGHT, WindowConstants.HIDE_ON_CLOSE)

  private lazy val p =
    for
      fr <- frame
      _ <- fr.getContentPane().add(mainPanel)
      _ <- fr.setVisible(true)
    yield ()
  p.runSyncUnsafe()

  def simulationPanel = _simulationPanel

  private def createFrame(title: String, width: Int, height: Int, closeOperation: Int): Task[JFrame] =
    for
      fr <- new JFrame(title)
      _ <- fr.setSize(width, height)
      _ <- fr.setLocationRelativeTo(null)
      _ <- fr.setResizable(false)
      _ <- fr.setDefaultCloseOperation(closeOperation)
      _ <- fr.setVisible(true)
    yield fr

  def updateParametersPanel(): Unit = mainPanel.updateParametersPanel()

  def updateDisplayedCar(): Unit =
    mainPanel.updateDisplayedCar()

  def updateDisplayedStanding(): Unit = _simulationPanel.updateDisplayedStanding()

  def updateFastestLapIcon(carName: String): Unit = _simulationPanel.updateFastestLapIcon(carName)
  
  def setFinalReportEnabled(): Unit =
    _simulationPanel.setFinalReportEnabled()

  def displaySimulationPanel(track: Track, standing: Standing): Unit = SwingUtilities.invokeLater { () =>
    lazy val p = for
      fr <- frame
      _ <- _simulationPanel.renderTrack(track)
      _ <- fr.getContentPane().removeAll()
      _ <- fr.getContentPane().add(_simulationPanel)
      _ <- fr.revalidate()
    yield ()
    p.runSyncUnsafe()
  }

  def displayStartingPositionsPanel(): Unit = SwingUtilities.invokeLater { () =>
    lazy val p = for
      fr <- startingPositionsFrame
      _ <- fr.getContentPane().add(startingPositionsPanel)
      _ <- fr.revalidate()
    yield ()
    p.runSyncUnsafe()
  }

  def displayEndRacePanel(): Unit = SwingUtilities.invokeLater { () =>
    val _endRacePanel = EndRacePanel(controller)
    lazy val p = for
      fr <- frame
      _ <- fr.getContentPane().removeAll()
      _ <- fr.getContentPane().add(_endRacePanel)
      _ <- fr.revalidate()
    yield ()
    p.runSyncUnsafe()
  }
