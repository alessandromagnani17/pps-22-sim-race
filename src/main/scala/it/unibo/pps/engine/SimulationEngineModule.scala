package it.unibo.pps.engine

import monix.execution.Scheduler.Implicits.global
import alice.tuprolog.{Term, Theory}
import it.unibo.pps.controller.ControllerModule
import it.unibo.pps.model.{
  Car,
  Direction,
  ModelModule,
  Phase,
  RenderCarParams,
  RenderStraightParams,
  Sector,
  Snapshot,
  Standings,
  Straight,
  Turn,
  Tyre,
  RenderParams
}
import it.unibo.pps.view.ViewModule
import monix.eval.Task
import monix.execution.Scheduler
import scala.{Tuple2 => Point2D}
import scala.io.StdIn.readLine
import concurrent.duration.{Duration, DurationDouble, DurationInt, FiniteDuration}
import scala.language.postfixOps
import it.unibo.pps.engine.SimulationConstants.*
import it.unibo.pps.prolog.Scala2P
import it.unibo.pps.utility.monadic.*
import it.unibo.pps.utility.GivenConversion.ModelConversion
import it.unibo.pps.utility.GivenConversion.GuiConversion.given_Conversion_Unit_Task
import it.unibo.pps.given
import it.unibo.pps.utility.PimpScala.RichInt.*
import it.unibo.pps.utility.PimpScala.RichTuple2.*
import it.unibo.pps.view.ViewConstants.*
import scala.math.BigDecimal
import scala.collection.mutable
import scala.collection.mutable.{HashMap, Map}

object SimulationEngineModule:
  trait SimulationEngine:

    /** Represents one simulation step
      * @return
      *   [[Task]]
      */
    def simulationStep: Task[Unit]

    /** Decreases simulation speed */
    def decreaseSpeed: Unit

    /** Increases simulation speed */
    def increaseSpeed: Unit

  trait Provider:
    val simulationEngine: SimulationEngine

  type Requirements = ViewModule.Provider with ModelModule.Provider with ControllerModule.Provider

  trait Component:
    context: Requirements =>
    class SimulationEngineImpl extends SimulationEngine:

      private val speedManager = SpeedManager()
      private val movementsManager = Movements()
      private val sectorTimes: HashMap[String, Int] = HashMap.from(context.model.cars.map(_.name -> 0))
      private val finalPositions = List((633, 272), (533, 272), (433, 272), (333, 272))
      private var carsArrived = 0

      private def getFinalPositions(car: Car): Point2D[Int, Int] =
        finalPositions(context.model.standings.standings.indexOf(car))

      override def decreaseSpeed: Unit =
        speedManager.decreaseSpeed

      override def increaseSpeed: Unit =
        speedManager.increaseSpeed

      override def simulationStep: Task[Unit] =
        for
          _ <- moveCars
          _ <- updateStandings
          _ <- updateView
          _ <- waitFor(speedManager.speed)
          _ <- checkEnd
        yield ()

      private def checkEnd: Task[Unit] =
        for
          _ <- io(
            if carsArrived == NUM_CARS then
              controller.notifyStop
              context.view.setFinalReportEnabled()
          )
        yield ()

      private def waitFor(simulationSpeed: Double): Task[Unit] =
        val time = BASE_TIME * simulationSpeed
        Task.sleep(time millis)

      private def moveCars: Task[Unit] =
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

      private def updateCar(car: Car, globalTime: Int): Car =
        for
          time <- io(sectorTimes(car.name))
          newVelocity <- io(updateVelocity(car, time))
          newPosition <- io(
            if car.actualLap > context.model.totalLaps then getFinalPositions(car) else updatePosition(car, globalTime)
          )
          newFuel <- io(updateFuel(car, newPosition))
          newDegradation <- io(Tyre.degradation(car.tyre, car.actualLap))
          newRenderParams <- io(car.renderCarParams.copy(position = newPosition))
        yield car.copy(
          actualSpeed = newVelocity,
          fuel = newFuel,
          degradation = newDegradation,
          renderCarParams = newRenderParams
        )

      private def updateParameter[E](sector: Sector, onStraight: () => E, onTurn: () => E): E = sector match
        case s: Straight => onStraight()
        case t: Turn => onTurn()

      private def updateFuel(car: Car, newPosition: Point2D[Int, Int]): Double =
        val onStraight = () =>
          val oldPosition = car.renderCarParams.position
          car.fuel - Car.decreaseFuel(Math.abs(oldPosition._1 - newPosition._1))
        val onTurn = () =>
          val r = computeRadius(car.actualSector.renderParams, car.renderCarParams.position)
          val teta = angleBetweenPoints(car.renderCarParams.position, newPosition, r)
          val l = circularArc(teta, r)
          car.fuel - Car.decreaseFuel(l)
        updateParameter(car.actualSector, onStraight, onTurn)

      private def updateVelocity(car: Car, time: Int): Int =
        val onStraight = () =>
          movementsManager
            .updateVelocityStraight(car, time, car.actualSector.phase(car.renderCarParams.position))
            .runSyncUnsafe()
        val onTurn = () =>
          movementsManager
            .updateVelocityTurn(car)
            .runSyncUnsafe()
        updateParameter(car.actualSector, onStraight, onTurn)

      private def updatePosition(car: Car, time: Int): Point2D[Int, Int] =
        updateParameter(car.actualSector, () => straightMovement(car, time), () => turnMovement(car, time))

      private def straightMovement(car: Car, time: Int): Point2D[Int, Int] =
        car.actualSector.phase(car.renderCarParams.position) match
          case Phase.Acceleration =>
            val p = movementsManager.updatePositionStraightAcceleration(car, sectorTimes(car.name))
            sectorTimes(car.name) = sectorTimes(car.name) + 1
            p
          case Phase.Deceleration =>
            val p = movementsManager.updatePositionStraightDeceleration(car, sectorTimes(car.name))
            sectorTimes(car.name) = sectorTimes(car.name) + 1
            checkEndStraight(car, p)
          case Phase.Ended =>
            sectorTimes(car.name) = BASE_SECTORTIME_TURN
            car.actualSector = context.model.track.nextSector(car.actualSector)
            turnMovement(car, time)

      private def checkEndStraight(car: Car, p: Point2D[Int, Int]): Point2D[Int, Int] =
        car.actualSector.renderParams match
          case RenderStraightParams(_, _, _, _, endX) =>
            val d = (p._1 - endX) * car.actualSector.direction
            if d >= 0 then (endX, p._2)
            else p

      private def turnMovement(car: Car, time: Int): Point2D[Int, Int] =
        car.actualSector.phase(car.renderCarParams.position) match
          case Phase.Acceleration =>
            val p = movementsManager.updatePositionTurn(
              car,
              sectorTimes(car.name),
              car.actualSpeed,
              car.actualSector.renderParams
            )
            sectorTimes(car.name) = sectorTimes(car.name) + 1
            p
          case Phase.Ended =>
            car.actualSector = context.model.track.nextSector(car.actualSector)
            sectorTimes(car.name) = BASE_SECTORTIME_STRAIGHT
            checkLap(car, time)
            straightMovement(car, time)
          case Phase.Deceleration => EMPTY_POSITION

      private def checkLap(car: Car, time: Int): Unit =
        if car.actualSector.id == 1 then
          car.actualLap = car.actualLap + 1
          car.lapTime = time - car.raceTime
          if car.lapTime < car.fastestLap || car.fastestLap == 0 then car.fastestLap = car.lapTime
          if car.lapTime < controller.fastestLap || controller.fastestLap == 0 then
            controller.fastestLap = car.lapTime
            controller.fastestCar = car.name
            context.view.updateFastestLapIcon(car.name)
          car.raceTime = time
        if car.actualLap > context.model.actualLap then context.model.actualLap = car.actualLap
        if car.actualLap > context.model.totalLaps then
          car.raceTime = time
          carsArrived = carsArrived + 1

    private def updateStandings: Task[Unit] =
      for
        lastSnap <- io(context.model.getLastSnapshot())
        newStandings = calcNewStandings(lastSnap)
        _ <- io(context.model.setS(newStandings))
        _ <- io(context.view.updateDisplayedStandings())
      yield ()

    private def calcNewStandings(snap: Snapshot): Standings =
      val carsByLap = snap.cars.groupBy(_.actualLap).sortWith(_._1 >= _._1)
      var l1: List[Car] = List.empty

      carsByLap.foreach(carsBySector => {
        carsBySector._2
          .groupBy(_.actualSector)
          .sortWith(_._1.id >= _._1.id)
          .foreach(e => {
            e._1 match
              case Straight(id, _, _) =>
                if id == 1 then l1 = l1.concat(sortCars(e._2, _ > _, true))
                else l1 = l1.concat(sortCars(e._2, _ < _, true))
              case Turn(id, _, _) =>
                if id == 2 then
                  l1 = l1.concat(sortCars(e._2.filter(_.renderCarParams.position._2 >= 390), _ < _, true))
                  l1 = l1.concat(
                    sortCars(
                      e._2.filter(c => c.renderCarParams.position._2 >= 175 && c.renderCarParams.position._2 < 390),
                      _ > _,
                      false
                    )
                  )
                  l1 = l1.concat(sortCars(e._2.filter(_.renderCarParams.position._2 < 175), _ > _, true))
                else
                  l1 = l1.concat(sortCars(e._2.filter(_.renderCarParams.position._2 < 175), _ > _, true))
                  l1 = l1.concat(
                    sortCars(
                      e._2.filter(c => c.renderCarParams.position._2 >= 175 && c.renderCarParams.position._2 < 390),
                      _ < _,
                      false
                    )
                  )
                  l1 = l1.concat(sortCars(e._2.filter(_.renderCarParams.position._2 >= 390), _ < _, true))
          })
      })
      Standings(l1)

    private def sortCars(cars: List[Car], f: (Int, Int) => Boolean, isHorizontal: Boolean): List[Car] =
      var l: List[Car] = List.empty
      if isHorizontal then
        cars
          .sortWith((c1, c2) => f(c1.renderCarParams.position._1, c2.renderCarParams.position._1))
          .foreach(e => l = l.concat(List(e)))
      else
        cars
          .sortWith((c1, c2) => f(c1.renderCarParams.position._2, c2.renderCarParams.position._2))
          .foreach(e => l = l.concat(List(e)))
      l

    private def updateView: Task[Unit] =
      for
        cars <- io(context.model.getLastSnapshot().cars)
        _ <- io(context.view.updateCars(cars, context.model.actualLap, context.model.totalLaps))
      yield ()

  trait Interface extends Provider with Component:
    self: Requirements =>
