package it.unibo.pps.model

import it.unibo.pps.model.Car
import it.unibo.pps.view.simulation_panel.DrawingCarParams
import monix.reactive.MulticastStrategy
import monix.reactive.subjects.ConcurrentSubject
import monix.execution.Scheduler.Implicits.global
import scala.collection.mutable.Map
import java.awt.Color
import monix.execution.{Ack, Cancelable}
import concurrent.{Future, Promise}

object ModelModule:
  trait Model:
    def track: Track
    def cars: List[Car]
    def startingPositions: Map[Int, Car]
    def currentCarIndex: Int
    def actualLap: Int
    def totalLaps: Int
    def standing: Standing
    def getLastSnapshot(): Snapshot
    def initSnapshot(): Unit
    def currentCarIndex_=(index: Int): Unit
    def startingPositions_=(startingPos: Map[Int, Car]): Unit
    def actualLap_=(lap: Int): Unit
    def totalLaps_(lap: Int): Unit
    def setS(standings: Standing): Unit
    def createStanding(): Unit
    def addSnapshot(snapshot: Snapshot): Unit
    def registerCallbackHistory(
        onNext: List[Snapshot] => Future[Ack],
        onError: Throwable => Unit,
        onComplete: () => Unit
    ): Cancelable

  trait Provider:
    val model: Model

  trait Component:
    class ModelImpl extends Model:

      private val _track = TrackBuilder().createBaseTrack()
      private var _cars: List[Car] = CarsLoader.load(track)

      /*TODO - togliere i campi _cars e _stading da fuori e farli vivere solo nella history */

      private var _standing: Standing = Standing(Map.from(cars.zipWithIndex.map { case (k, v) => (v, k) }))
      private var history: List[Snapshot] = List.empty
      private var _currentCarIndex = 0
      private var _startingPositions: Map[Int, Car] = Map(0 -> cars.head, 1 -> cars(1), 2 -> cars(2), 3 -> cars(3))
      private var _actualLap = 1
      private val historySubject = ConcurrentSubject[List[Snapshot]](MulticastStrategy.publish)
      private var _totalLaps = 10

      override def registerCallbackHistory(
          onNext: List[Snapshot] => Future[Ack],
          onError: Throwable => Unit,
          onComplete: () => Unit
      ): Cancelable =
        historySubject.subscribe(onNext, onError, onComplete)

      override def currentCarIndex: Int = _currentCarIndex
      override def cars: List[Car] = _cars
      override def startingPositions: Map[Int, Car] = _startingPositions
      override def track: Track = _track
      override def actualLap: Int = _actualLap
      override def totalLaps: Int = _totalLaps
      override def standing: Standing = _standing
      override def getLastSnapshot(): Snapshot = history.last
      override def addSnapshot(snapshot: Snapshot): Unit =
        history = history :+ snapshot
        historySubject.onNext(history)
      override def currentCarIndex_=(index: Int): Unit = _currentCarIndex = index
      override def startingPositions_=(startingPos: Map[Int, Car]): Unit = _startingPositions = startingPos
      override def actualLap_=(lap: Int): Unit = _actualLap = lap
      override def setS(standings: Standing): Unit = _standing = standings

      override def initSnapshot(): Unit =
        val c = _cars
          .map(car => car.copy(maxSpeed = car.maxSpeed - car.tyre))
        addSnapshot(Snapshot(c, 0))

      override def totalLaps_(lap: Int): Unit = _totalLaps = lap

      override def createStanding(): Unit = _standing = Standing(startingPositions)

  trait Interface extends Provider with Component
