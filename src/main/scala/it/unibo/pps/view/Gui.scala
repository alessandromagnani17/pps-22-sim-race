package it.unibo.pps.view

import it.unibo.pps.controller.ControllerModule
import it.unibo.pps.model.car.Car
import it.unibo.pps.view.main_panel.MainPanel
import it.unibo.pps.view.main_panel.StartingPositionsPanel
import it.unibo.pps.view.simulation_panel.SimulationPanel
import monix.eval.Task

import java.awt.{Color, Component, Toolkit}
import javax.swing.{JFrame, JTable, SwingUtilities, WindowConstants}
import monix.execution.Scheduler.Implicits.global
import it.unibo.pps.model.Standings
import it.unibo.pps.model.track.Track
import it.unibo.pps.view.Constants.GuiConstants.*
import it.unibo.pps.view.end_race_panel.EndRacePanel

class Gui(controller: ControllerModule.Controller):

  import it.unibo.pps.utility.GivenConversion.GuiConversion.given

  private var mainPanel = MainPanel(controller)
  private var _simulationPanel: SimulationPanel = SimulationPanel(controller)
  private var startingPositionsPanel = StartingPositionsPanel(controller)
  private val frame = createFrame("sim-race", FRAME_WIDTH, FRAME_HEIGHT, WindowConstants.EXIT_ON_CLOSE)
  private val startingPositionsFrame =
    createFrame(
      "starting-positions",
      STARTING_POS_FRAME_WIDTH,
      STARTING_POS_FRAME_HEIGHT,
      WindowConstants.HIDE_ON_CLOSE
    )

  private lazy val p =
    for
      fr <- frame
      _ <- fr.getContentPane().add(mainPanel)
      _ <- fr.setVisible(true)
    yield ()
  p.runSyncUnsafe()

  /** Returns the simulation panel */
  def simulationPanel: SimulationPanel = _simulationPanel

  private def createFrame(title: String, width: Int, height: Int, closeOperation: Int): Task[JFrame] =
    for
      fr <- new JFrame(title)
      _ <- fr.setSize(width, height)
      _ <- fr.setLocationRelativeTo(null)
      _ <- fr.setResizable(false)
      _ <- fr.setDefaultCloseOperation(closeOperation)
      _ <- fr.setVisible(true)
    yield fr

  /** Method that updates the displayed parameters when the car displayed is changed */
  def updateParametersPanel: Unit = mainPanel.updateParametersPanel

  /** Method that updates the car displayed */
  def updateDisplayedCar: Unit =
    mainPanel.updateDisplayedCar

  /** Method that updates the displayed standings */
  def updateDisplayedStandings: Unit = _simulationPanel.updateDisplayedStandings

  /** Method that updates the fastest lap icon
    * @param carName
    *   The name of the car that has made the fastest lap
    */
  def updateFastestLapIcon(carName: String): Unit = _simulationPanel.updateFastestLapIcon(carName)

  /** Method that sets enabled the final report button that if pressed, display the end race panel */
  def setFinalReportEnabled: Unit =
    _simulationPanel.setFinalReportEnabled

  /** Method that displays the simulation panel
    * @param track
    *   The track to be rendered before the display of the simulation panel
    */
  def displaySimulationPanel(track: Track, car: List[Car], actualLap: Int, totalLap: Int): Unit =
    SwingUtilities.invokeLater { () =>
      lazy val p = for
        fr <- frame
        _ <- _simulationPanel.renderTrack(track)
        _ <- _simulationPanel.render(car, actualLap, totalLap)
        _ <- fr.getContentPane().removeAll()
        _ <- fr.getContentPane().add(_simulationPanel)
        _ <- fr.revalidate()
      yield ()
      p.runSyncUnsafe()
    }

  def reset: Unit = SwingUtilities.invokeLater { () =>
    startingPositionsPanel = StartingPositionsPanel(controller)
    mainPanel = MainPanel(controller)
    _simulationPanel = SimulationPanel(controller)
    lazy val p = for
      fr <- frame
      _ <- fr.getContentPane().removeAll()
      _ <- fr.getContentPane().add(mainPanel)
      _ <- fr.revalidate()
    yield ()
    p.runSyncUnsafe()
  }

  /** Method that displays the starting positions panel */
  def displayStartingPositionsPanel: Unit = SwingUtilities.invokeLater { () =>
    lazy val p = for
      fr <- startingPositionsFrame
      _ <- fr.getContentPane().removeAll()
      _ <- fr.getContentPane().add(startingPositionsPanel)
      _ <- fr.revalidate()
    yield ()
    p.runSyncUnsafe()
  }

  /** Method that displays the end race panel */
  def displayEndRacePanel: Unit = SwingUtilities.invokeLater { () =>
    val _endRacePanel = EndRacePanel(controller)
    lazy val p = for
      fr <- frame
      _ <- fr.getContentPane().removeAll()
      _ <- fr.getContentPane().add(_endRacePanel)
      _ <- fr.revalidate()
    yield ()
    p.runSyncUnsafe()
  }

  /** Returns the initial list of cars */
  def getInitialList: List[String] = controller.cars.map(_.name)
