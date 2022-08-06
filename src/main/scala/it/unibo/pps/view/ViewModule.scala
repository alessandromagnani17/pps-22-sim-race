package it.unibo.pps.view

import it.unibo.pps.controller.ControllerModule

object ViewModule:
  trait View:
    def updateDisplayedCar(carIndex: Int, tyresType: String): Unit
  
  trait Provider:
    val view: View
  
  type Requirements = ControllerModule.Provider
  
  trait Component:
    context: Requirements =>
    class ViewImpl extends View:
      val gui = new Gui(1300, 700, context.controller)

      override def updateDisplayedCar(carIndex: Int, tyresType: String): Unit =
        gui.changeCar(carIndex, tyresType)

  
  trait Interface extends Provider with Component:
    self: Requirements =>
