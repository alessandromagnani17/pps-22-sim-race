package it.unibo.pps.engine

import it.unibo.pps.model.ModelModule
import it.unibo.pps.view.ViewModule

object SimulationEngineModule:
  trait SimulationEngine
  
  trait Provider: 
    val simulationEngine: SimulationEngine
  
  type Requirements = ViewModule.Provider with ModelModule.Provider
  
  trait Component:
    context: Requirements =>
    class SimulationEngineImpl extends SimulationEngine
    
    
  trait Interface extends Provider with Component:
    self: Requirements =>
