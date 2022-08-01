package it.unibo.pps.controller

import it.unibo.pps.engine.SimulationEngineModule
import it.unibo.pps.model.ModelModule

object ControllerModule:
  trait Controller:
    def notifyStart(): Unit
    
  trait Provider:
    val controller: Controller
    
  type Requirements = ModelModule.Provider with SimulationEngineModule.Provider
  
  trait Component:
    context: Requirements =>
    class ControllerImpl extends Controller:
      def notifyStart(): Unit = ???
      
  trait Interface extends Provider with Component:
    self: Requirements => 

