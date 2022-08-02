package it.unibo.pps.view

import it.unibo.pps.controller.ControllerModule

object ViewModule:
  trait View
  
  trait Provider:
    val view: View
  
  type Requirements = ControllerModule.Provider
  
  trait Component:
    context: Requirements =>
    class ViewImpl extends View:
      val gui = new Gui(700, 700, context.controller)

  
  trait Interface extends Provider with Component:
    self: Requirements =>
