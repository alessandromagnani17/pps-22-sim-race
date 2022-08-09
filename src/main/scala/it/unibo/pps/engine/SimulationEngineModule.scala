package it.unibo.pps.engine

import it.unibo.pps.model.ModelModule
import it.unibo.pps.view.ViewModule
import monix.eval.Task

given Conversion[Unit, Task[Unit]] = Task(_)

object SimulationEngineModule:
  trait SimulationEngine:
    def simulationStep(): Task[Unit]

  trait Provider:
    val simulationEngine: SimulationEngine

  type Requirements = ViewModule.Provider with ModelModule.Provider

  trait Component:
    context: Requirements =>
    class SimulationEngineImpl extends SimulationEngine:
      override def simulationStep(): Task[Unit] = ???

      private def moveCar(): Task[Unit] = ???
      private def updateCharts(): Task[Unit] = ???
      private def updateStanding(): Task[Unit] = ???
      private def updateView(): Task[Unit] = ???

  trait Interface extends Provider with Component:
    self: Requirements =>
