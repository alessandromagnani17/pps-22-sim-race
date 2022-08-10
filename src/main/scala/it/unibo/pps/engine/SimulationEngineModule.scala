package it.unibo.pps.engine

import it.unibo.pps.model.ModelModule
import it.unibo.pps.view.ViewModule
import monix.eval.Task
import monix.execution.Scheduler
import concurrent.duration.{Duration, DurationInt, FiniteDuration, DurationDouble}
import scala.language.postfixOps
import it.unibo.pps.engine.SimulationConstants.*
import it.unibo.pps.utility.monadic._

object SimulationEngineModule:
  trait SimulationEngine:
    def simulationStep(): Task[Unit]
    def decreaseSpeed(): Unit
    def increaseSpeed(): Unit

  trait Provider:
    val simulationEngine: SimulationEngine

  type Requirements = ViewModule.Provider with ModelModule.Provider

  trait Component:
    context: Requirements =>
    class SimulationEngineImpl extends SimulationEngine:

      private val speedManager = SpeedManager()

      override def decreaseSpeed(): Unit =
        speedManager.decreaseSpeed()

      override def increaseSpeed(): Unit =
        speedManager.increaseSpeed()

      override def simulationStep(): Task[Unit] =
        for
          _ <- moveCars()
          _ <- updateStanding()
          _ <- updateCharts()
          _ <- updateView()
          _ <- waitFor(speedManager._simulationSpeed)
        yield ()

      private def waitFor(simulationSpeed: Double): Task[Unit] =
        val time = BASE_TIME * simulationSpeed
        Task.sleep(time millis)

      private def moveCars(): Task[Unit] =
        for _ <- io(println("Updating cars.... " + speedManager._simulationSpeed))
        yield ()

      private def updateCharts(): Task[Unit] =
        for _ <- io(println("Updating charts...."))
        yield ()

      private def updateStanding(): Task[Unit] =
        for _ <- io(println("Updating standing...."))
        yield ()

      private def updateView(): Task[Unit] =
        for _ <- io(println("Updating view...."))
        yield ()

  trait Interface extends Provider with Component:
    self: Requirements =>
