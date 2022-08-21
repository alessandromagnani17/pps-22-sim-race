package it.unibo.pps.engine

import alice.tuprolog.{Term, Theory}
import it.unibo.pps.controller.ControllerModule
import it.unibo.pps.model.{Car, ModelModule, Snapshot}
import it.unibo.pps.view.ViewModule
import monix.eval.Task
import monix.execution.Scheduler

import scala.io.StdIn.readLine
import concurrent.duration.{Duration, DurationDouble, DurationInt, FiniteDuration}
import scala.language.postfixOps
import it.unibo.pps.engine.SimulationConstants.*
import it.unibo.pps.prolog.Scala2P
import it.unibo.pps.utility.monadic.*
import it.unibo.pps.utility.GivenConversion.ModelConversion
import it.unibo.pps.view.simulation_panel.DrawingCarParams
import it.unibo.pps.utility.GivenConversion.GuiConversion.given_Conversion_Unit_Task
import it.unibo.pps.utility.PimpScala.RichInt.*
import it.unibo.pps.utility.PimpScala.RichTuple2.*
import it.unibo.pps.view.ViewConstants.*
import it.unibo.pps.view.simulation_panel.DrawingTurnParams

import scala.collection.mutable.HashMap

given Conversion[String, Term] = Term.createTerm(_)
given Conversion[Seq[_], Term] = _.mkString("[", ",", "]")
given Conversion[String, Theory] = Theory.parseLazilyWithStandardOperators(_)
given Itearable2List[E]: Conversion[Iterable[E], List[E]] = _.toList

object SimulationEngineModule:
  trait SimulationEngine:
    def simulationStep(): Task[Unit]
    def decreaseSpeed(): Unit
    def increaseSpeed(): Unit

  trait Provider:
    val simulationEngine: SimulationEngine

  type Requirements = ViewModule.Provider with ModelModule.Provider with ControllerModule.Provider

  trait Component:
    context: Requirements =>
    class SimulationEngineImpl extends SimulationEngine:

      private val speedManager = SpeedManager()
      private val engine = Scala2P.createEngine("/prolog/movements.pl")
      //private var time0 = 1
      private var times0: HashMap[String, Int] = HashMap.empty

      override def decreaseSpeed(): Unit =
        speedManager.decreaseSpeed()

      override def increaseSpeed(): Unit =
        speedManager.increaseSpeed()

      override def simulationStep(): Task[Unit] =
        for
          _ <- moveCars()
          //_ <- updateStanding()
          //_ <- updateCharts()
          _ <- updateView()
          _ <- waitFor(speedManager._simulationSpeed)
        yield ()

      private def waitFor(simulationSpeed: Double): Task[Unit] =
        val time = BASE_TIME * simulationSpeed
        Task.sleep(time millis)

      private def moveCars(): Task[Unit] =
        for
          //_ <- io(println("Updating cars.... " + speedManager._simulationSpeed))
          lastSnap <- getLastSnapshot()
          newSnap <- updatePositions(lastSnap)
          _ <- io(context.model.addSnapshot(newSnap))
        yield ()

      private def updatePositions(snapshot: Snapshot): Task[Snapshot] =
        for
          time <- io(snapshot.time + 1)
          cars <- io(snapshot.cars)
          newCars = for
            car <- cars
            position = car.drawingCarParams.position
            newPosition = calcWithProlog(car, position._1, time, car.actualSpeed)
            d = DrawingCarParams(newPosition, car.drawingCarParams.color)
          yield car.copy(drawingCarParams = d)
          newSnap <- io(Snapshot(newCars, time))
        yield newSnap

      private def calcWithProlog(car: Car, x: Int, time: Int, velocity: Double): Tuple2[Int, Int] =
        if car.drawingCarParams.position._1 < 725 then
          if car.drawingCarParams.position._1 < 500 then

            if time == 1 then
              car.maxSpeed = (car.maxSpeed / 3.6).toInt //pixel/s, assumendo che 1km = 1000pixel e 1h = 3600sec

            val newVelocity = engine(s"computeNewVelocity(${car.actualSpeed}, ${car.acceleration}, $time,  Ns)")
              .map(Scala2P.extractTermToString(_, "Ns"))
              .toSeq
              .head
              .toDouble
              .toInt

            if newVelocity < car.maxSpeed then car.actualSpeed = newVelocity

            val newP = engine(s"computeNewPositionForStraight($x, $velocity, $time, ${car.acceleration}, Np)")
              .map(Scala2P.extractTermToString(_, "Np"))
              .toSeq
              .head
              .toDouble
              .toInt

            if newP >= 725 then (725, car.drawingCarParams.position._2)
            else (newP, car.drawingCarParams.position._2)
          else

            if time == 1 then
              car.maxSpeed = (car.maxSpeed / 3.6).toInt //pixel/s, assumendo che 1km = 1000pixel e 1h = 3600sec

            val newVelocity = engine(s"computeNewVelocityDeceleration(${car.actualSpeed}, 1, $time, Ns)")
              .map(Scala2P.extractTermToString(_, "Ns"))
              .toSeq
              .head
              .toDouble
              .toInt

            if newVelocity < car.maxSpeed then car.actualSpeed = newVelocity

            val newP = engine(s"computeNewPositionForStraight($x, $velocity, $time, 1, Np)")
              .map(Scala2P.extractTermToString(_, "Np"))
              .toSeq
              .head
              .toDouble
              .toInt

            if newP >= 725 then
              times0 = times0 + (car.name -> 0)
              (725, car.drawingCarParams.position._2)
            else (newP, car.drawingCarParams.position._2)
        else
          //readLine()
          val t0 = times0.get(car.name).get
          val teta_t = 0.5 * car.acceleration * (t0 ** 2)
          times0(car.name) = times0(car.name) + 1
          val r = car.radius
          val newX = 725 + r * Math.sin(Math.toRadians(teta_t))
          val newY = 283 - r * Math.cos(Math.toRadians(teta_t))
          val np = (newX.toInt, newY.toInt)
          checkBounds(np, (725, 283), 170)

      private def checkBounds(p3: (Int, Int), center: (Int, Int), r: Int): (Int, Int) =
        var dx = (p3._1 + 12, p3._2) euclideanDistance center
        var dy = (p3._1, p3._2 + 12) euclideanDistance center
        if dx - r < 0 then dx = r
        if dy - r < 0 then dy = r
        if dx >= r || dy >= r then (p3._1 - (dx - r), p3._2 - (dy - r))
        else p3

      private def getLastSnapshot(): Task[Snapshot] =
        io(context.model.getLastSnapshot())

      private def updateCharts(): Task[Unit] =
        for _ <- io(println("Updating charts...."))
        yield ()

      private def updateStanding(): Task[Unit] =
        for _ <- io(println("Updating standing...."))
        yield ()

      private def updateView(): Task[Unit] =
        for
          //_ <- io(println("Updating view...."))
          cars <- io(context.model.getLastSnapshot().cars)
          _ <- io(context.view.updateCars(cars))
        yield ()

  trait Interface extends Provider with Component:
    self: Requirements =>
