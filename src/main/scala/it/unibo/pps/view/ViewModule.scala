package it.unibo.pps.view

import it.unibo.pps.controller.ControllerModule
import it.unibo.pps.model.Track
import it.unibo.pps.view.ViewConstants.*
import it.unibo.pps.model.Car

object ViewModule:
  trait View:
    def updateDisplayedCar(carIndex: Int, tyresType: String): Unit
    def displaySimulationPanel(track: Track): Unit
    def updateCars(cars: List[Car]): Unit

  trait Provider:
    val view: View

  type Requirements = ControllerModule.Provider

  trait Component:
    context: Requirements =>
    class ViewImpl extends View:
      val gui = new Gui(FRAME_WIDTH, FRAME_HEIGHT, context.controller)

      override def updateDisplayedCar(carIndex: Int, tyresType: String): Unit =
        gui.updateDisplayedCar(carIndex, tyresType)

      override def displaySimulationPanel(track: Track): Unit =
        gui.displaySimulationPanel(track)

      override def updateCars(cars: List[Car]): Unit =
        gui.updateCars(cars)

  trait Interface extends Provider with Component:
    self: Requirements =>
