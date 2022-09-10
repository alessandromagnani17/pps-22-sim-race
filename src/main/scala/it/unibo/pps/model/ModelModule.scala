package it.unibo.pps.model

import it.unibo.pps.model.car.Car
import monix.reactive.MulticastStrategy
import monix.reactive.subjects.ConcurrentSubject
import monix.execution.Scheduler.Implicits.global

import scala.collection.mutable.Map
import java.awt.Color
import monix.execution.{Ack, Cancelable}

import javax.management.relation.InvalidRelationTypeException
import concurrent.{Future, Promise}
import it.unibo.pps.model.loader.{CarsLoader, TrackLoader}
import it.unibo.pps.model.track.Track

object ModelModule:
  trait Model:

    /** The current track */
    def track: Track

    /** The initial cars */
    def cars: List[Car]

    def resetModel: Unit

    def startingPositions: List[Car]
    def currentCarIndex: Int
    def actualLap: Int
    def totalLaps: Int
    def standings: Standings
    def fastestLap: Int
    def fastestCar: String
    def getLastSnapshot: Snapshot

    /** Initializes simulation history */
    def initSnapshot: Unit
    def currentCarIndex_=(index: Int): Unit
    def startingPositions_=(startingPos: List[Car]): Unit
    def actualLap_=(lap: Int): Unit
    def totalLaps_(lap: Int): Unit
    def fastestLap_=(lap: Int): Unit
    def fastestCar_=(carName: String): Unit
    def setS(standings: Standings): Unit
    def createStandings: Unit

    /** Adds one snapshot to the history
      * @param snapshot
      *   Simulation snapshot
      */
    def addSnapshot(snapshot: Snapshot): Unit

    /** Registers the given callbacks to the history
      * @param onNext
      *   Callback to be executed when history is updated
      * @param onError
      *   Callback to be executed when an error occurs
      * @params
      *   onComplete Callback to be executed when the stream is closed
      */
    def registerCallbackHistory(
        onNext: List[Snapshot] => Future[Ack],
        onError: Throwable => Unit,
        onComplete: () => Unit
    ): Cancelable

  trait Provider:
    val model: Model

  trait Component:
    class ModelImpl extends Model:

      private val _track = TrackLoader("/prolog/basetrack.pl").load
      private var _cars: List[Car] = CarsLoader("/prolog/cars.pl", track).load

      private var _standings: Standings = Standings(_cars)
      private var history: List[Snapshot] = List.empty
      private var _currentCarIndex = 0
      private var _startingPositions: List[Car] = _cars
      private var _actualLap = 1
      private var historySubject = ConcurrentSubject[List[Snapshot]](MulticastStrategy.publish)
      private var _totalLaps = 15
      private var _fastestLap = 0
      private var _fastestCar = ""

      override def registerCallbackHistory(
          onNext: List[Snapshot] => Future[Ack],
          onError: Throwable => Unit,
          onComplete: () => Unit
      ): Cancelable =
        historySubject.subscribe(onNext, onError, onComplete)

      override def currentCarIndex: Int = _currentCarIndex
      override def cars: List[Car] = _cars

      override def resetModel: Unit =
        history = List.empty
        historySubject = ConcurrentSubject[List[Snapshot]](MulticastStrategy.publish)
        _fastestLap = 0
        _fastestCar = ""
        _actualLap = 1
        _cars = CarsLoader("/prolog/cars.pl", track).load
        _startingPositions = _cars
        _standings = Standings(_cars)
        _currentCarIndex = 0
        _totalLaps = 15

      override def startingPositions: List[Car] = _startingPositions
      override def track: Track = _track
      override def actualLap: Int = _actualLap
      override def totalLaps: Int = _totalLaps
      override def standings: Standings = _standings
      override def fastestLap: Int = _fastestLap
      override def fastestCar: String = _fastestCar
      override def getLastSnapshot: Snapshot = history.last
      override def addSnapshot(snapshot: Snapshot): Unit =
        history = history :+ snapshot
        historySubject.onNext(history)
      override def currentCarIndex_=(index: Int): Unit = _currentCarIndex = index
      override def startingPositions_=(startingPos: List[Car]): Unit = _startingPositions = startingPos
      override def actualLap_=(lap: Int): Unit = _actualLap = lap
      override def setS(standings: Standings): Unit = _standings = standings

      override def initSnapshot: Unit =
        addSnapshot(Snapshot(_cars, 0))

      override def totalLaps_(lap: Int): Unit = _totalLaps = lap

      override def fastestLap_=(lap: Int): Unit = _fastestLap = lap

      override def fastestCar_=(carName: String): Unit = _fastestCar = carName

      override def createStandings: Unit = _standings = Standings(startingPositions)

  trait Interface extends Provider with Component
