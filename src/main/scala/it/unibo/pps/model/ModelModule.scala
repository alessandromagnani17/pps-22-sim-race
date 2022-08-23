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
    def actualLap: Int
    def standing: Standing
    def getLastSnapshot(): Snapshot
    def initSnapshot(): Unit
    def currentCarIndex_=(index: Int): Unit
    def startingPositions_=(startingPos: Map[Int, Car]): Unit
    def actualLap_=(lap: Int): Unit
    def updateStanding(): Unit
    def addSnapshot(snapshot: Snapshot): Unit

  trait Provider:
    val model: Model

  trait Component:
    class ModelImpl extends Model:

      private val _track = TrackBuilder().createBaseTrack()

      /*private var _cars: List[Car] = List(
        Car(
          "/cars/3-hard.png",
          "McLaren",
          Tyre.SOFT,
          Driver(1, 1),
          200,
          0,
          2,
          _track.sectors.head,
          128,
          DrawingCarParams((313, 155), Color.GREEN)
          //DrawingCarParams((725, 155), Color.GREEN)
        ),
        Car(
          "/cars/2-hard.png",
          "Red Bull",
          Tyre.SOFT,
          Driver(1, 1),
          200,
          0,
          2,
          _track.sectors.head,
          141,
          DrawingCarParams((293, 142), Color.BLUE)
          //DrawingCarParams((725, 142), Color.BLUE)
        ),
        Car(
          "/cars/1-hard.png",
          "Mercedes",
          Tyre.SOFT,
          Driver(1, 1),
          200,
          0,
          2,
          _track.sectors.head,
          154,
          DrawingCarParams((273, 129), Color.CYAN)
          //DrawingCarParams((725, 129), Color.CYAN)
        ),
        Car(
          "/cars/0-hard.png",
          "Ferrari",
          Tyre.SOFT,
          Driver(1, 1),
          200,
          0,
          2,
          _track.sectors.head,
          168,
          DrawingCarParams((253, 115), Color.RED)
          //DrawingCarParams((725, 115), Color.RED)
        )
      )*/

      private var _cars: List[Car] = List(
        Car(
          "/cars/0-hard.png",
          "Ferrari",
          Tyre.SOFT,
          Driver(1, 1),
          200,
          1,
          0,
          2,
          _track.sectors.head,
          128,
          DrawingCarParams((313, 155), Color.RED)
          //DrawingCarParams((725, 115), Color.RED)
        ),
        Car(
          "/cars/1-hard.png",
          "Mercedes",
          Tyre.SOFT,
          Driver(1, 1),
          200,
          1,
          0,
          2,
          _track.sectors.head,
          141,
          DrawingCarParams((293, 142), Color.CYAN)
          //DrawingCarParams((725, 129), Color.CYAN)
        ),
        Car(
          "/cars/2-hard.png",
          "Red Bull",
          Tyre.SOFT,
          Driver(1, 1),
          200,
          1,
          0,
          2,
          _track.sectors.head,
          154,
          DrawingCarParams((273, 129), Color.BLUE)
          //DrawingCarParams((725, 142), Color.BLUE)
        ),
        Car(
          "/cars/3-hard.png",
          "McLaren",
          Tyre.SOFT,
          Driver(1, 1),
          200,
          1,
          0,
          2,
          _track.sectors.head,
          168,
          DrawingCarParams((253, 115), Color.GREEN)
          //DrawingCarParams((725, 155), Color.GREEN)
        )
      )
      

      /*TODO - togliere i campi _cars e _stading da fuori e farli vivere solo nella history */

      private var _standing: Standing = Standing(cars)
      private var history: List[Snapshot] = List.empty
      private var _currentCarIndex = 0
      private var _startingPositions: Map[Int, Car] = Map(0 -> cars.head, 1 -> cars(1), 2 -> cars(2), 3 -> cars(3))
      private var _actualLap = 1

      override def currentCarIndex: Int = _currentCarIndex
      override def cars: List[Car] = _cars
      override def startingPositions: Map[Int, Car] = _startingPositions
      override def track: Track = _track
      override def actualLap: Int = _actualLap
      override def standing: Standing = _standing
      override def getLastSnapshot(): Snapshot = history.last
      override def addSnapshot(snapshot: Snapshot): Unit = history = history :+ snapshot
      override def currentCarIndex_=(index: Int): Unit = _currentCarIndex = index
      override def startingPositions_=(startingPos: Map[Int, Car]): Unit = _startingPositions = startingPos
      override def actualLap_=(lap: Int): Unit = _actualLap = lap
      override def initSnapshot(): Unit = addSnapshot(Snapshot(cars, 0))
      override def updateStanding(): Unit = _standing = Standing(startingPositions.toList.map(e => e._2))

  trait Interface extends Provider with Component
