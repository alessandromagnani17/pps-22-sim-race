package it.unibo.pps.view

import it.unibo.pps.controller.ControllerModule
import it.unibo.pps.model.Tyre

object ViewModule:
  trait View:
    def updateDisplayedCar(): Unit
    def displaySimulationPanel(): Unit
    def displayStartingPositionsPanel(): Unit
    def updateParametersPanel(): Unit

  trait Provider:
    val view: View

  type Requirements = ControllerModule.Provider

  trait Component:
    context: Requirements =>
    class ViewImpl extends View:
      val gui = new Gui(1296, 810, context.controller)

      override def updateDisplayedCar(): Unit =
        gui.updateDisplayedCar()

      override def displaySimulationPanel(): Unit =
        gui.displaySimulationPanel()

      override def displayStartingPositionsPanel(): Unit =
        gui.displayStartingPositionsPanel()

      override def updateParametersPanel(): Unit =
        gui.updateParametersPanel()

  trait Interface extends Provider with Component:
    self: Requirements =>
