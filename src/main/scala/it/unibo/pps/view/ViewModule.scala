package it.unibo.pps.view

import it.unibo.pps.controller.ControllerModule
import it.unibo.pps.model.{Car, Snapshot, Standing, Track, Tyre}
import it.unibo.pps.view.ViewConstants.*

object ViewModule:
  trait View:
    def updateDisplayedCar(): Unit
    def updateDisplayedStanding(): Unit
    def displayStartingPositionsPanel(): Unit
    def updateParametersPanel(): Unit
    def displaySimulationPanel(track: Track, standing: Standing): Unit
    def displayEndRacePanel(): Unit
    def updateCars(cars: List[Car]): Unit
    def updateCharts(l: List[Snapshot]): Unit
    def setFinalReportEnabled(): Unit

  trait Provider:
    val view: View

  type Requirements = ControllerModule.Provider

  trait Component:
    context: Requirements =>
    class ViewImpl extends View:
      val gui = new Gui(FRAME_WIDTH, FRAME_HEIGHT, context.controller)

      override def updateDisplayedCar(): Unit =
        gui.updateDisplayedCar()

      override def updateDisplayedStanding(): Unit = gui.updateDisplayedStanding()

      override def displaySimulationPanel(track: Track, standing: Standing): Unit =
        gui.displaySimulationPanel(track, standing)

      override def updateCars(cars: List[Car]): Unit =
        gui.simulationPanel.render(cars)

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

  trait Interface extends Provider with Component:
    self: Requirements =>
