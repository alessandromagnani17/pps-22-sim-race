package it.unibo.pps.engine

import it.unibo.pps.model.{ModelModule, Snapshot, Car}
import it.unibo.pps.view.ViewModule
import monix.eval.Task
import monix.execution.Scheduler

import concurrent.duration.{Duration, DurationDouble, DurationInt, FiniteDuration}
import scala.language.postfixOps
import it.unibo.pps.engine.SimulationConstants.*
import it.unibo.pps.utility.monadic.*
import it.unibo.pps.utility.GivenConversion.ModelConversion.given


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
        for
          _ <- io(println("Updating cars.... " + speedManager._simulationSpeed))
          // Come prova solo la prima macchina
          ls <- getLastSnapshot()
          newVel <- computeNewVelocity(ls)
          _ <- context.model.cars.head.velocity = newVel // aggiorno velocità della macchina
          /*newPos <- computeNewPositionPL(
            context.model.cars.head,
            ls.time
          )*/
        yield ()

      private def getLastSnapshot(): Task[Snapshot] =
        context.model.getLastSnapshot()

      private def computeNewVelocity(s: Snapshot): Task[Double] =
        Car.computeNewVelocity(8.0, s.cars.head, s.time + 1)

      /*private def computeNewPositionPL(
                                        car: Car,
                                        time: Int
                                      ): Task[Tuple2[Double, Double]] =
        val newX = context.prologEngine.calcNewPosition(
          car.position._1,
          car.velocity,
          time + 1,
          8.0
        )
        (newX, car.drawingCarParams.position._2)*/

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
