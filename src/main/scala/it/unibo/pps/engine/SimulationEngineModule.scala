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
  RenderParams,
  RenderStraightParams,
  RenderTurnParams,
  Sector,
  Snapshot,
  Standings,
  Straight,
  Turn,
  Tyre
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
import it.unibo.pps.utility.GivenConversion.DirectionGivenConversion.given
import it.unibo.pps.utility.PimpScala.RichInt.*
import it.unibo.pps.utility.PimpScala.RichTuple2.*
import scala.math.BigDecimal
import scala.collection.mutable
import scala.collection.mutable.{HashMap, Map}
import it.unibo.pps.view.Constants.MainPanelConstants.NUM_CARS

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

    def resetEngine: Unit

  trait Provider:
    val simulationEngine: SimulationEngine

  type Requirements = ViewModule.Provider with ModelModule.Provider with ControllerModule.Provider

  trait Component:
    context: Requirements =>
    class SimulationEngineImpl extends SimulationEngine:

      private val speedManager = SpeedManager()
      private val movementsManager = Movements()
      private var sectorTimes: HashMap[String, Int] = HashMap.from(context.model.cars.map(_.name -> 0))
      private val finalPositions = List((633, 272), (533, 272), (433, 272), (333, 272))
      private var carsArrived = 0

      private def getFinalPositions(car: Car): Point2D[Int, Int] =
        finalPositions(context.model.standings.standings.indexOf(car))

      override def resetEngine: Unit =
        sectorTimes = HashMap.from(context.model.cars.map(_.name -> 0))
        carsArrived = 0
        speedManager.reset

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
              context.view.setFinalReportEnabled
          )
        yield ()

      private def waitFor(simulationSpeed: Double): Task[Unit] =
        val time = BASE_TIME * simulationSpeed
        Task.sleep(time millis)

      private def moveCars: Task[Unit] =
        for
          lastSnap <- io(context.model.getLastSnapshot)
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
          lastSnap <- io(context.model.getLastSnapshot)
          newStandings = calcNewStandings(lastSnap)
          _ <- io(context.model.standings = newStandings)
          _ <- io(context.view.updateDisplayedStandings)
        yield ()

      private def calcNewStandings(snap: Snapshot): Standings =
        val carsByLap = snap.cars.groupBy(_.actualLap).sortWith(_._1 >= _._1)
        var newPositions: List[Car] = List.empty

        carsByLap.foreach(carsBySector => {
          carsBySector._2
            .groupBy(_.actualSector)
            .sortWith(_._1.id >= _._1.id)
            .foreach(cars => {
              cars._1 match
                case Straight(id, _, _) => newPositions = newPositions.concat(calcStraightStandings(cars))
                case Turn(id, _, _) => newPositions = newPositions.concat(calcTurnStandings(cars))
            })
        })
        Standings(newPositions)

      private def sortCars(cars: List[Car], f: (Car, Car) => Boolean): List[Car] =
        cars.sortWith((c1, c2) => f(c1, c2))

      private def calcStraightStandings(cars: (Sector, List[Car])): List[Car] =
        cars._1.direction match
          case Direction.Forward =>
            sortCars(cars._2, (c1, c2) => c1.renderCarParams.position._1 > c2.renderCarParams.position._1)
          case Direction.Backward =>
            sortCars(cars._2, (c1, c2) => c1.renderCarParams.position._1 < c2.renderCarParams.position._1)

      private def calcTurnStandings(cars: (Sector, List[Car])): List[Car] =
        val topTurnCars: List[Car] = calcTopTurnStandings(cars._2)
        val centerTurnCars: List[Car] = calcCenterTurnStandings(cars._2, cars._1.direction)
        val bottomTurnCars: List[Car] = calcBottomTurnStandings(cars._2)

        cars._1.direction match
          case Direction.Forward => bottomTurnCars ++ centerTurnCars ++ topTurnCars
          case Direction.Backward => topTurnCars ++ centerTurnCars ++ bottomTurnCars

      private def calcTopTurnStandings(cars: List[Car]): List[Car] =
        sortCars(
          cars.filter(c =>
            c.renderCarParams.position._2 < c.actualSector.renderParams.asInstanceOf[RenderTurnParams].topLimit
          ),
          (c1, c2) => c1.renderCarParams.position._1 > c2.renderCarParams.position._1
        )

      private def calcBottomTurnStandings(cars: List[Car]): List[Car] =
        sortCars(
          cars.filter(c =>
            c.renderCarParams.position._2 >= c.actualSector.renderParams.asInstanceOf[RenderTurnParams].bottomLimit
          ),
          (c1, c2) => c1.renderCarParams.position._1 < c2.renderCarParams.position._1
        )

      private def calcCenterTurnStandings(cars: List[Car], direction: Direction): List[Car] =
        val filteredCars: List[Car] = cars.filter(c =>
          c.renderCarParams.position._2 >= c.actualSector.renderParams.asInstanceOf[RenderTurnParams].topLimit &&
            c.renderCarParams.position._2 < c.actualSector.renderParams.asInstanceOf[RenderTurnParams].bottomLimit
        )
        direction match
          case Direction.Forward =>
            sortCars(filteredCars, (c1, c2) => c1.renderCarParams.position._2 > c2.renderCarParams.position._2)
          case Direction.Backward =>
            sortCars(filteredCars, (c1, c2) => c1.renderCarParams.position._2 < c2.renderCarParams.position._2)

      private def updateView: Task[Unit] =
        for
          cars <- io(context.model.getLastSnapshot.cars)
          _ <- io(context.view.updateRender(cars, context.model.actualLap, context.model.totalLaps))
        yield ()

  trait Interface extends Provider with Component:
    self: Requirements =>
