package it.unibo.pps.engine

import monix.execution.Scheduler.Implicits.global
import alice.tuprolog.{Term, Theory}
import it.unibo.pps.controller.ControllerModule
import it.unibo.pps.model.{Car, ModelModule, Phase, Snapshot, Straight, Turn}
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
import it.unibo.pps.view.simulation_panel.{DrawingCarParams, DrawingParams, DrawingStraightParams, DrawingTurnParams}
import it.unibo.pps.utility.GivenConversion.GuiConversion.given_Conversion_Unit_Task
import it.unibo.pps.utility.PimpScala.RichInt.*
import it.unibo.pps.utility.PimpScala.RichTuple2.*
import it.unibo.pps.view.ViewConstants.*
import it.unibo.pps.model.Sector

import scala.collection.mutable
import scala.collection.mutable.{HashMap, Map}

given Itearable2List[E]: Conversion[Iterable[E], List[E]] = _.toList
given Conversion[Task[(Int, Int)], (Int, Int)] = _.runSyncUnsafe()
given Conversion[Task[Car], Car] = _.runSyncUnsafe()

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
      private val movementsManager = PrologMovements()
      private var sectorTimes: HashMap[String, Int] =
        HashMap(("Ferrari" -> 0), ("McLaren" -> 0), ("Red Bull" -> 0), ("Mercedes" -> 0))

      private val angles = TurnAngles()

      override def decreaseSpeed(): Unit =
        speedManager.decreaseSpeed()

      override def increaseSpeed(): Unit =
        speedManager.increaseSpeed()

      override def simulationStep(): Task[Unit] =
        for
          _ <- moveCars()
          _ <- updateStanding()
          _ <- updateView()
          _ <- waitFor(speedManager._simulationSpeed)
        yield ()

      private def waitFor(simulationSpeed: Double): Task[Unit] =
        val time = BASE_TIME * simulationSpeed
        Task.sleep(time millis)

      private def moveCars(): Task[Unit] =
        for
          lastSnap <- io(context.model.getLastSnapshot())
          newSnap <- computeNewSnapshot(lastSnap)
          _ <- io(context.model.addSnapshot(newSnap))
        yield ()

      private def computeNewSnapshot(snapshot: Snapshot): Task[Snapshot] =
        for
          time <- io(snapshot.time + 1)
          cars <- io(snapshot.cars)
          newCars <- io(cars.map(updateCar(_, time)))
          newSnap <- io(Snapshot(newCars, time))
        yield newSnap

      private def updateCar(car: Car, time: Int): Car =
        for
          newVelocity <- io(updateVelocity(car, time))
          newPosition <- io(updatePosition(car, time))
          newFuel <- io(updateFuel(car, newPosition))
          newDegradation <- io(updateDegradation(car, newPosition, newVelocity))
          newDrawingParams <- io(car.drawingCarParams.copy(position = newPosition))
        yield car.copy(
          actualSpeed = newVelocity,
          fuel = newFuel,
          degradation = newDegradation,
          drawingCarParams = newDrawingParams
        )

      private val computeRadius = (d: DrawingParams, position: Tuple2[Int, Int]) =>
        d match {
          case DrawingTurnParams(center, _, _, _, _, _, _) =>
            center euclideanDistance position
          case _ => 0
        }

      private def updateFuel(car: Car, newPosition: Tuple2[Int, Int]): Double = car.actualSector match {
        case Straight(_, _) =>
          val oldPosition = car.drawingCarParams.position
          car.fuel - Math.abs(oldPosition._1 - newPosition._1) * 0.0015
        case Turn(_, _) =>
          val r = computeRadius(car.actualSector.drawingParams, car.drawingCarParams.position)
          val teta = angles.difference(car.name)
          val l = (teta / 360) * 2 * r * Math.PI
          car.fuel - l * 0.0015
      }

      private def updateDegradation(car: Car, newPosition: Tuple2[Int, Int], v: Double): Double =
        val f = (d: Double, s: Double, v: Double, l: Int) => if d >= 50 then d else d + (s + v + l) / 3500
        car.actualSector match {
          case Straight(_, _) =>
            val oldPosition = car.drawingCarParams.position
            f(car.degradation, Math.abs(oldPosition._1 - newPosition._1) * 2, v, car.actualLap)
          case Turn(_, _) =>
            val r = computeRadius(car.actualSector.drawingParams, car.drawingCarParams.position)
            val teta = angles.difference(car.name)
            val l = (teta / 360) * 2 * r * Math.PI
            f(car.degradation, l, v, car.actualLap)
        }

      private def updateVelocity(car: Car, time: Int): Double = car.actualSector match {
        case Straight(_, _) =>
          car.actualSector.phase(car.drawingCarParams.position) match {
            case Phase.Acceleration =>
              val v = movementsManager.newVelocityStraightAcc(car, sectorTimes(car.name))
              if v > car.maxSpeed then car.maxSpeed else v
            case Phase.Deceleration => movementsManager.newVelocityStraightDec(car, sectorTimes(car.name))
            case _ => car.actualSpeed
          }
        case Turn(_, _) =>
          car.actualSector.phase(car.drawingCarParams.position) match {
            case Phase.Acceleration => car.actualSpeed
            case _ => 6 //TODO - magic number
          }
      }

      private def updatePosition(car: Car, time: Int): Tuple2[Int, Int] = car.actualSector match {
        case Straight(_, _) => straightMovement(car, time)
        case Turn(_, _) => turnMovement(car, time)
      }

      private def straightMovement(car: Car, time: Int): Tuple2[Int, Int] =
        car.actualSector.phase(car.drawingCarParams.position) match {
          case Phase.Acceleration => acceleration(car, time)
          case Phase.Deceleration => deceleration(car, time)
          case Phase.Ended =>
            car.actualSector = context.model.track.nextSector(car.actualSector)
            checkLap(car)
            turnMovement(car, time)
        }

      private def checkLap(car: Car): Unit =
        if car.actualSector.id == 1 then car.actualLap = car.actualLap + 1
        if car.actualLap > context.model.actualLap then context.model.actualLap = car.actualLap

      private def turnMovement(car: Car, time: Int): Tuple2[Int, Int] =
        car.actualSector.phase(car.drawingCarParams.position) match {
          case Phase.Acceleration => turn(car, time, car.actualSpeed, car.actualSector.drawingParams)
          case Phase.Ended =>
            car.actualSector = context.model.track.nextSector(car.actualSector)
            sectorTimes(car.name) = 0
            angles.reset(car.name)
            checkLap(car)
            straightMovement(car, time)
          case Phase.Deceleration => (0, 0)
        }

      private def acceleration(car: Car, time: Int): Task[(Int, Int)] =
        for
          x <- io(car.drawingCarParams.position._1)
          i <- io(if car.actualSector.id == 1 then 1 else -1)
          velocity <- io(car.actualSpeed)
          time <- io(sectorTimes.get(car.name).get)
          acceleration <- io(car.acceleration)
          newP <- io(movementsManager.newPositionStraight(x, velocity, time, acceleration, i))
          _ <- io(sectorTimes(car.name) = sectorTimes(car.name) + 1)
        yield (newP, car.drawingCarParams.position._2)

      private def deceleration(car: Car, time: Int): Task[Tuple2[Int, Int]] =
        for
          x <- io(car.drawingCarParams.position._1)
          i <- io(if car.actualSector.id == 1 then 1 else -1)
          newP <- io(movementsManager.newPositionStraight(x, car.actualSpeed, sectorTimes.get(car.name).get, 1, i))
          p <- io(car.actualSector.drawingParams match {
            case DrawingStraightParams(_, _, _, _, endX) => //TODO - fare un metodo di check
              val d = (newP - endX) * i
              if d >= 0 then
                sectorTimes(car.name) = 0
                (endX, car.drawingCarParams.position._2)
              else (newP, car.drawingCarParams.position._2)
          })
        yield p

      private def turn(car: Car, time: Int, velocity: Double, d: DrawingParams): Tuple2[Int, Int] = d match {
        case DrawingTurnParams(center, _, _, _, _, direction, _) =>
          val x = car.drawingCarParams.position._1
          val t0 = sectorTimes(car.name)
          val teta_t = 0.5 * car.acceleration * (t0 ** 2)
          angles.setAngle(teta_t, car.name)
          sectorTimes(car.name) = sectorTimes(car.name) + 1
          val r = car.drawingCarParams.position euclideanDistance center
          var newX = 0.0
          var newY = 0.0
          var np = (0, 0)
          if direction == 1 then
            newX = center._1 + (r * Math.sin(Math.toRadians(teta_t)))
            newY = center._2 - (r * Math.cos(Math.toRadians(teta_t)))
            np = (newX.toInt, newY.toInt)
            println(s"${car.name}")
            np = checkBounds(np, center, 170, direction)
            if np._1 < 725 then np = (724, np._2) // TODO - migliorare
          else
            newX = center._1 + (r * Math.sin(Math.toRadians(teta_t + 180)))
            newY = center._2 - (r * Math.cos(Math.toRadians(teta_t + 180)))
            np = (newX.toInt, newY.toInt)
            np = checkBounds(np, center, 170, direction)
            if np._1 > 181 then np = (182, np._2) // TODO - migliorare
          np
      }

      private def checkBounds(p: (Int, Int), center: (Int, Int), r: Int, direction: Int): (Int, Int) =
        var dx = (p._1 + 12, p._2) euclideanDistance center
        var dy = (p._1, p._2 + 12) euclideanDistance center
        val rI = 113
        if dx - r < 0 && direction == 1 then dx = r
        if dy - r < 0 && direction == 1 then dy = r
        if (dx >= r || dy >= r) && direction == 1 then (p._1 - (dx - r), p._2 - (dy - r))
        else if (dx <= rI || dy <= rI) && direction == -1 then
          println(s"dx-r: ${dx - rI} ---- dy-r: ${dy - rI} ")
          (p._1 + (dx - rI), p._2 + (dy - rI))
        else p

      private def updateStanding(): Task[Unit] =
        for
          lastSnap <- io(context.model.getLastSnapshot())
          newStartingPositions = calcNewStanding(lastSnap)
          _ <- io(context.model.startingPositions = Map.from(newStartingPositions))
          _ <- io(context.view.updateDisplayedStanding())
        yield ()

      private def calcNewStanding(snap: Snapshot): Map[Int, Car] =
        val x: List[(Sector, List[Car])] = snap.cars.groupBy(_.actualSector).sortWith(_._1.id >= _._1.id)
        var l1: List[Car] = List.empty
        x.foreach(e => {
          e._1 match
            case Straight(id, _) =>
              if id == 1 then l1 = l1.concat(sortCars(e._2, _ > _, true))
              else l1 = l1.concat(sortCars(e._2, _ < _, true))
            case Turn(id, _) =>
              if id == 2 then l1 = l1.concat(sortCars(e._2, _ > _, false))
              else l1 = l1.concat(sortCars(e._2, _ < _, false))
        })
        Map.from(l1.zipWithIndex.map { case (k, v) => (v, k) })

      private def sortCars(cars: List[Car], f: (Int, Int) => Boolean, isHorizontal: Boolean): List[Car] =
        var l: List[Car] = List.empty
        if isHorizontal then
          cars
            .sortWith((c1, c2) => f(c1.drawingCarParams.position._1, c2.drawingCarParams.position._1))
            .foreach(e => l = l.concat(List(e)))
        else
          cars
            .sortWith((c1, c2) => f(c1.drawingCarParams.position._2, c2.drawingCarParams.position._2))
            .foreach(e => l = l.concat(List(e)))
        l

      private def updateView(): Task[Unit] =
        for
          cars <- io(context.model.getLastSnapshot().cars)
          _ <- io(context.view.updateCars(cars))
        yield ()

  trait Interface extends Provider with Component:
    self: Requirements =>
