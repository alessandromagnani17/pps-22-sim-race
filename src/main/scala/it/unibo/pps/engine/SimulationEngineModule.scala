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
import scala.collection.mutable.{HashMap, Map}

given Itearable2List[E]: Conversion[Iterable[E], List[E]] = _.toList
given Conversion[Task[(Int, Int)], (Int, Int)] = _.runSyncUnsafe()

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
      private var times0: HashMap[String, Int] = HashMap.empty

      ///
      private var curvaFatta: Boolean = false
      ///

      override def decreaseSpeed(): Unit =
        speedManager.decreaseSpeed()

      override def increaseSpeed(): Unit =
        speedManager.increaseSpeed()

      override def simulationStep(): Task[Unit] =
        for
          _ <- moveCars()
          _ <- updateStanding()
          //_ <- updateCharts()
          _ <- updateView()
          _ <- waitFor(speedManager._simulationSpeed)
        yield ()

      private def waitFor(simulationSpeed: Double): Task[Unit] =
        val time = BASE_TIME * simulationSpeed

        Task.sleep(time millis)

      private def moveCars(): Task[Unit] =
        for
          lastSnap <- io(context.model.getLastSnapshot())
          newSnap <- updatePositions(lastSnap)
          _ <- io(context.model.addSnapshot(newSnap))
        yield ()

      private def updatePositions(snapshot: Snapshot): Task[Snapshot] =
        for
          time <- io(snapshot.time + 1)
          cars <- io(snapshot.cars)
          newCars = for
            car <- cars
            newPosition = findNewPosition(car, time)
            d = car.drawingCarParams.copy(position = newPosition)
          yield car.copy(drawingCarParams = d)
          newSnap <- io(Snapshot(newCars, time))
        yield newSnap

      private def findNewPosition(car: Car, time: Int): Tuple2[Int, Int] = car.actualSector match {
        case Straight(_, _) => straightMovement(car, time)
        case Turn(_, _) => turnMovement(car, time)
      }

      private def straightMovement(car: Car, time: Int): Tuple2[Int, Int] =
        car.actualSector.phase(car.drawingCarParams.position) match {
          case Phase.Acceleration => acc(car, time, car.actualSpeed)
          case Phase.Deceleration => dec(car, time, car.actualSpeed)
          case Phase.Ended =>

            if car.name == "Ferrari" then
              println("---------- CURVA ----------")

            car.actualSector = context.model.track.nextSector(car.actualSector)
            turnMovement(car, time)
        }

      private def turnMovement(car: Car, time: Int): Tuple2[Int, Int] =
        car.actualSector.phase(car.drawingCarParams.position) match {
          case Phase.Acceleration => turn(car, time, car.actualSpeed, car.actualSector.drawingParams)
          case Phase.Ended =>
             curvaFatta = true
            car.actualSector = context.model.track.nextSector(car.actualSector)
            car.actualSpeed = 20
            straightMovement(car, time)
          case Phase.Deceleration => (0, 0)
        }

      private def acc(car: Car, time: Int, velocity: Double): Task[(Int, Int)] =
        for

          _ <- io(
            if car.name == "Ferrari" then
              println("ActualSpeed: " + velocity)
              println("Time: "+ time)
              if curvaFatta then
                println("Times0: "+ times0.get("Ferrari").get)
          )

          x <- io(car.drawingCarParams.position._1)
          _ <- io(if time == 1 then car.maxSpeed = (car.maxSpeed / 3.6).toInt)

          newVelocity <- io(
            if curvaFatta then
              println("curvaFatta: "+curvaFatta)
              movementsManager.newVelocityStraightAcc(car, times0.get(car.name).get, car.acceleration)
            else
              println("curvaFatta: "+curvaFatta)
              movementsManager.newVelocityStraightAcc(car, time, car.acceleration)
          )
          //x <- io(car.drawingCarParams.position._1)
          //_ <- io(if time == 1 then car.maxSpeed = (car.maxSpeed / 3.6).toInt)
          //newVelocity <- io(movementsManager.newVelocityStraightAcc(car, time, car.acceleration))
          _ <- io(if newVelocity < car.maxSpeed then car.actualSpeed = newVelocity)
          i <- io(if car.actualSector.id == 1 then 1 else -1)
          newP <- io(movementsManager.newPositionStraight(x, velocity, time, car.acceleration, i))
        yield (newP, car.drawingCarParams.position._2)

      private def dec(car: Car, time: Int, velocity: Double): Task[Tuple2[Int, Int]] =
        for
          x <- io(car.drawingCarParams.position._1)
          _ <- io(if time == 1 then car.maxSpeed = (car.maxSpeed / 3.6).toInt)
          newVelocity <- io(movementsManager.newVelocityStraightDec(car, time, 1))
          _ <- io(if newVelocity < car.maxSpeed then car.actualSpeed = newVelocity)
          i <- io(if car.actualSector.id == 1 then 1 else -1)
          newP <- io(movementsManager.newPositionStraight(x, velocity, time, 1, i))
          p <- io(car.actualSector.drawingParams match {
            case DrawingStraightParams(_, _, _, _, endX) =>
              val d = (newP - endX) * i
              if d >= 0 then
                times0(car.name) = 0
                (endX, car.drawingCarParams.position._2)
              else (newP, car.drawingCarParams.position._2)
          })
        yield p

      private def turn(car: Car, time: Int, velocity: Double, d: DrawingParams): Tuple2[Int, Int] =
        d match {
          case DrawingTurnParams(center, _, _, _, _, direction, _) =>
            val x = car.drawingCarParams.position._1
            val t0 = times0.get(car.name).get
            val teta_t = 0.5 * car.acceleration * (t0 ** 2)
            times0(car.name) = times0(car.name) + 1
            val r = car.radius
            var newX = 0.0
            var newY = 0.0
            var np = (0, 0)
            if direction == 1 then
              newX = center._1 + (r * Math.sin(Math.toRadians(teta_t)))
              newY = center._2 - (r * Math.cos(Math.toRadians(teta_t)))
              np = (newX.toInt, newY.toInt)
              np = checkBounds(np, center, 170, direction)
            else
              newX = center._1 + (r * Math.sin(Math.toRadians(teta_t + 180)))
              newY = center._2 - (r * Math.cos(Math.toRadians(teta_t + 180)))
              np = (newX.toInt, newY.toInt)
              np = checkBounds(np, center, 170, direction)
            np

        }

      private def checkBounds(p: (Int, Int), center: (Int, Int), r: Int, direction: Int): (Int, Int) =
        var dx = 0
        var dy = 0
        if direction == 1 then
          dx = (p._1 + 12, p._2) euclideanDistance center
          dy = (p._1, p._2 + 12) euclideanDistance center
        else
          dx = (p._1 - 12, p._2) euclideanDistance center
          dy = (p._1, p._2 + 12) euclideanDistance center
        if dx - r < 0 then dx = r
        if dy - r < 0 then dy = r
        if dx >= r || dy >= r then (p._1 - (dx - r), p._2 - (dy - r))
        else p

      private def updateCharts(): Task[Unit] =
        for _ <- context.view.updateDisplayedStanding()
          yield ()

      private def updateStanding(): Task[Unit] =
        for
          lastSnap <- io(context.model.getLastSnapshot())
          newStartingPositions = calcNewStanding(lastSnap)
          _ <- io(context.model.startingPositions = Map.from(newStartingPositions))
          _ <- io(context.view.updateDisplayedStanding())
        yield ()

      private def calcNewStanding(snap: Snapshot): Map[Int, Car] =
        // Funzionamento:
        // Raggruppo le macchine per settore, poi ordino i settori dal più grande al più piccolo
        // Dopo in base al settore guardo x o y
        // NB Probabilmente ci sarà da mettere il giro per ogni macchina perchè altrimenti
        // questo meccanismo non funziona ( quando una macchina completa il primo giro in prima posizione diventa ultima)
        // Se mettessimo il giro per ogni macchina prima di raggruppare per settori raggruppiamo per giri in ordine decrescente
        // e dopo credo che funzionerebbe
        var x: List[(Sector, List[Car])] = snap.cars.groupBy(_.actualSector).sortWith(_._1.id >= _._1.id)
        var l1: List[Car] = List.empty
        x.foreach(e => {
          e._1 match
            case Straight(id, _) =>
              if id == 1 then e._2.sortWith(_.drawingCarParams.position._1 > _.drawingCarParams.position._1).foreach(e => l1 = l1.concat(List(e)))
              else e._2.sortWith(_.drawingCarParams.position._1 < _.drawingCarParams.position._1).foreach(e => l1 = l1.concat(List(e)))
            case Turn(id, _) =>
              if id == 2 then e._2.sortWith(_.drawingCarParams.position._2 > _.drawingCarParams.position._2).foreach(e => l1 = l1.concat(List(e)))
              else e._2.sortWith(_.drawingCarParams.position._2 < _.drawingCarParams.position._2).foreach(e => l1 = l1.concat(List(e)))
        })
        Map.from(l1.zipWithIndex.map{ case (k,v) => (v,k) })
      
      private def updateView(): Task[Unit] =
        for
          cars <- io(context.model.getLastSnapshot().cars)
          _ <- io(context.view.updateCars(cars))
        yield ()

  trait Interface extends Provider with Component:
    self: Requirements =>
