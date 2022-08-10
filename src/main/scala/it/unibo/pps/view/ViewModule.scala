package it.unibo.pps.view

import it.unibo.pps.controller.ControllerModule
import it.unibo.pps.view.ViewConstants.*

object ViewModule:
  trait View:
    def updateDisplayedCar(carIndex: Int, tyresType: String): Unit
    def displaySimulationPanel(): Unit

  trait Provider:
    val view: View

  type Requirements = ControllerModule.Provider

  trait Component:
    context: Requirements =>
    class ViewImpl extends View:
      val gui = new Gui(FRAME_WIDTH, FRAME_HEIGHT, context.controller)

      override def updateDisplayedCar(carIndex: Int, tyresType: String): Unit =
        gui.updateDisplayedCar(carIndex, tyresType)

      override def displaySimulationPanel(): Unit =
        gui.displaySimulationPanel()

  trait Interface extends Provider with Component:
    self: Requirements =>
