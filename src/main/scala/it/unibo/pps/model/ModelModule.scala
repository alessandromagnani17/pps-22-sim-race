package it.unibo.pps.model

import it.unibo.pps.model.Car
import it.unibo.pps.view.simulation_panel.DrawingCarParams

import java.awt.Color

object ModelModule:
  trait Model:
    def track: Track
    def cars: List[Car]
    def startingPositions: scala.collection.mutable.Map[Int, String]
    def currentCarIndex: Int
    def standing: Standing
    def getLastSnapshot(): Snapshot
    def currentCarIndex_=(index: Int): Unit
    def addSnapshot(snapshot: Snapshot): Unit

  trait Provider:
    val model: Model

  trait Component:
    class ModelImpl extends Model:

      private val _track = TrackBuilder().createBaseTrack()

      /*TODO - Le macchine non vanno create così ma i parametri vanno inseriti usando quelli presi da initial panel
        TODO - le posizioni di partenza devono essere quelle dello starting point corrispondente
        TODO - in base all'ordine di partenza scelto dall'utente
       */
      private val _cars = List(
        Car("/cars/0-hard.png", "Ferrari", Tyre.HARD, Driver(1, 1), 200, 0, DrawingCarParams((453, 115), Color.CYAN)),
        Car("/cars/1-hard.png", "Mercedes", Tyre.HARD, Driver(1, 1), 200, 0, DrawingCarParams((473, 129), Color.RED)),
        Car("/cars/2-hard.png", "Red Bull", Tyre.HARD, Driver(1, 1), 200, 0, DrawingCarParams((493, 142), Color.BLUE)),
        Car("/cars/3-hard.png", "McLaren", Tyre.HARD, Driver(1, 1), 200, 0, DrawingCarParams((513, 155), Color.GREEN))
      )
      private val _standing: Standing = Standing(cars)

      /*TODO - togliere i campi _cars e _stading da fuori e farli vivere solo nella history */
      private var history: List[Snapshot] = List(Snapshot(_cars, 0))
      private var _currentCarIndex = 0
      private val _startingPositions: scala.collection.mutable.Map[Int, String] = scala.collection.mutable.Map(0 -> "Ferrari", 1 -> "Mercedes", 2 -> "Red Bull", 3 -> "McLaren")

      override def currentCarIndex: Int = _currentCarIndex
      override def cars: List[Car] = _cars
      override def startingPositions: scala.collection.mutable.Map[Int, String] = _startingPositions
      override def track: Track = _track
      override def standing: Standing = _standing
      override def getLastSnapshot(): Snapshot = history.last
      override def currentCarIndex_=(index: Int): Unit = _currentCarIndex = index
      override def addSnapshot(snapshot: Snapshot): Unit = history = history :+ snapshot

  trait Interface extends Provider with Component
