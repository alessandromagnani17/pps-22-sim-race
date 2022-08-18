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

class Gui(width: Int, height: Int, controller: ControllerModule.Controller):

  import it.unibo.pps.utility.GivenConversion.GuiConversion.given

  controller.createCars()

  private val mainPanel = MainPanel(width, height, controller)
  private val _simulationPanel = SimulationPanel(width, height, controller)
  private val startingPositionsPanel = StartingPositionsPanel((width * 0.4).toInt, (height * 0.4).toInt, controller)
  private val frame = createFrame("sim-race", width, height, WindowConstants.EXIT_ON_CLOSE)
  private val startingPositionsFrame = createFrame("starting-positions", (width * 0.35).toInt, (height * 0.45).toInt, WindowConstants.HIDE_ON_CLOSE)

  def simulationPanel = _simulationPanel


  private lazy val p =
    for
      fr <- frame
      _ <- fr.getContentPane().add(mainPanel)
      _ <- fr.setVisible(true)
    yield ()
  p.runSyncUnsafe()

  private def createFrame(title: String, width: Int, height: Int, closeOperation: Int): Task[JFrame] =
    for
      fr <- new JFrame(title)
      _ <- fr.setSize(width, height)
      _ <- fr.setLocationRelativeTo(null)
      _ <- fr.setResizable(true)
      _ <- fr.setDefaultCloseOperation(closeOperation)
      _ <- fr.setVisible(true)
    yield fr

  def updateParametersPanel(): Unit = mainPanel.updateParametersPanel()

  def updateDisplayedCar(): Unit =
    mainPanel.updateDisplayedCar()

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

  def displayStartingPositionsPanel(): Unit = SwingUtilities.invokeLater { () =>
    val p = for
      fr <- startingPositionsFrame
      _ <- fr.getContentPane().add(startingPositionsPanel)
      _ <- fr.revalidate()
    yield ()
    p.runSyncUnsafe()
  }


