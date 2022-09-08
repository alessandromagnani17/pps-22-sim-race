package it.unibo.pps.view

import it.unibo.pps.controller.ControllerModule
import it.unibo.pps.model.{Car, Snapshot, Standings, Track, Tyre}
import it.unibo.pps.view.ViewConstants.*

object ViewModule:
  trait View:

    /**  Method that updates the car displayed */
    def updateDisplayedCar(): Unit

    /**  Method that updates the displayed standings */
    def updateDisplayedStandings(): Unit

    /**  Method that displays the StartingPositionsPanel */
    def displayStartingPositionsPanel(): Unit

    /**  Method that displays the SimulationPanel */
    def displaySimulationPanel(track: Track, standings: Standings): Unit
    
    /**  Method that displays the EndRacePanel */
    def displayEndRacePanel(): Unit

    /**  Method that updates the displayed parameters when the car displayed is changed */
    def updateParametersPanel(): Unit
    
    
    def updateCars(cars: List[Car], actualLap: Int, totalLaps: Int): Unit
    def updateCharts(l: List[Snapshot]): Unit
    def setFinalReportEnabled(): Unit
    def updateFastestLapIcon(carName: String): Unit

  trait Provider:
    val view: View

  type Requirements = ControllerModule.Provider

  trait Component:
    context: Requirements =>
    class ViewImpl extends View:
      val gui = new Gui(context.controller)

      override def updateDisplayedCar(): Unit =
        gui.updateDisplayedCar()

      override def updateDisplayedStandings(): Unit = gui.updateDisplayedStandings()

      override def displaySimulationPanel(track: Track, standings: Standings): Unit =
        gui.displaySimulationPanel(track, standings)

      override def updateCars(cars: List[Car], actualLap: Int, totalLaps: Int): Unit =
        gui.simulationPanel.render(cars, actualLap, totalLaps)

      override def displayStartingPositionsPanel(): Unit =
        gui.displayStartingPositionsPanel()

      override def displayEndRacePanel(): Unit =
        gui.displayEndRacePanel()

      override def updateParametersPanel(): Unit =
        gui.updateParametersPanel()

      override def updateCharts(l: List[Snapshot]): Unit =
        gui.simulationPanel.updateCharts(l.last)

      override def setFinalReportEnabled(): Unit =
        gui.setFinalReportEnabled()

      override def updateFastestLapIcon(carName: String): Unit =
        gui.updateFastestLapIcon(carName)

  trait Interface extends Provider with Component:
    self: Requirements =>
