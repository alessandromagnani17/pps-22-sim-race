package it.unibo.pps.controller

import it.unibo.pps.engine.SimulationEngineModule
import it.unibo.pps.model.ModelModule
import it.unibo.pps.view.ViewModule

object ControllerModule:
  trait Controller:
    def notifyStart(): Unit
    def updateDisplayedCar(tyresType: String): Unit
    def setCurrentCarIndex(index: Int): Unit
    def displaySimulationPanel(): Unit

  trait Provider:
    val controller: Controller

  type Requirements = ModelModule.Provider with SimulationEngineModule.Provider with ViewModule.Provider

  trait Component:
    context: Requirements =>
    class ControllerImpl extends Controller:
      private var currentCarIndex = 0

      def notifyStart(): Unit = ???
      def setCurrentCarIndex(index: Int): Unit = currentCarIndex = index

      def updateDisplayedCar(tyresType: String): Unit =
        context.view.updateDisplayedCar(currentCarIndex, tyresType)

      def displaySimulationPanel(): Unit =
        context.view.displaySimulationPanel()

  trait Interface extends Provider with Component:
    self: Requirements =>
