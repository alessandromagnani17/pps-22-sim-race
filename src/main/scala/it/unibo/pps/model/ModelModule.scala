package it.unibo.pps.model

import it.unibo.pps.model.Car
import it.unibo.pps.view.simulation_panel.DrawingCarParams
import scala.collection.mutable.Map

import java.awt.Color

object ModelModule:
  trait Model:
    def track: Track
    def cars: List[Car]
    def startingPositions: Map[Int, Car]
    def currentCarIndex: Int
    def standing: Standing
    def getLastSnapshot(): Snapshot
    def initSnapshot(): Unit
    def currentCarIndex_=(index: Int): Unit
    def updateStanding(): Unit
    def addSnapshot(snapshot: Snapshot): Unit

  trait Provider:
    val model: Model

  trait Component:
    class ModelImpl extends Model:

      private val _track = TrackBuilder().createBaseTrack()
      
      private val _cars = List(
        Car("/cars/0-hard.png", "Ferrari", Tyre.SOFT, Driver(1, 1), 200, 0, 2, _track.sectors.head, DrawingCarParams((253, 115), Color.RED)),
        Car("/cars/1-hard.png", "Mercedes", Tyre.SOFT, Driver(1, 1), 200, 0, 2, _track.sectors.head, DrawingCarParams((273, 129), Color.CYAN)),
        Car("/cars/2-hard.png", "Red Bull", Tyre.SOFT, Driver(1, 1), 200, 0, 2, _track.sectors.head, DrawingCarParams((293, 142), Color.BLUE)),
        Car("/cars/3-hard.png", "McLaren", Tyre.SOFT, Driver(1, 1), 200, 0, 2, _track.sectors.head, DrawingCarParams((313, 155), Color.GREEN))
      )

      private var _standing: Standing = Standing(cars)

      /*TODO - togliere i campi _cars e _stading da fuori e farli vivere solo nella history */

      private var history: List[Snapshot] = List.empty
      private var _currentCarIndex = 0
      private val _startingPositions: Map[Int, Car] = Map(0 -> cars.head, 1 -> cars(1), 2 -> cars(2), 3 -> cars(3))

      override def currentCarIndex: Int = _currentCarIndex
      override def cars: List[Car] = _cars
      override def startingPositions: Map[Int, Car] = _startingPositions
      override def track: Track = _track
      override def standing: Standing = _standing
      override def getLastSnapshot(): Snapshot = history.last
      override def addSnapshot(snapshot: Snapshot): Unit = history = history :+ snapshot
      override def currentCarIndex_=(index: Int): Unit = _currentCarIndex = index
      override def initSnapshot(): Unit = addSnapshot(Snapshot(cars,0))
      override def updateStanding(): Unit = _standing = Standing(startingPositions.toList.map(e => e._2))

  trait Interface extends Provider with Component
