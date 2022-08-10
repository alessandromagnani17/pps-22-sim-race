package it.unibo.pps.controller

import it.unibo.pps.engine.SimulationEngineModule
import it.unibo.pps.model.{Car, Driver, ModelModule, Tyre}
import it.unibo.pps.view.ViewModule

object ControllerModule:
  trait Controller:
    def notifyStart(): Unit
    def createCars(): Unit
    def getCurrentCar(): Car
    def updateDisplayedCar(tyresType: String): Unit
    def setCurrentCarIndex(index: Int): Unit
    def displaySimulationPanel(): Unit

  trait Provider:
    val controller: Controller

  type Requirements = ModelModule.Provider with SimulationEngineModule.Provider with ViewModule.Provider

  trait Component:
    context: Requirements =>
    class ControllerImpl extends Controller:
      private val numCars = 4
      private val carNames = List("Ferrari", "Mercedes", "Red Bull", "McLaren")
      private var currentCarIndex = 0
      private var cars: List[Car] = List.empty

      def notifyStart(): Unit = ???
      def setCurrentCarIndex(index: Int): Unit = currentCarIndex = index

      def getCurrentCar(): Car = cars(currentCarIndex)

      def createCars(): Unit =
        val l = for
          index <- 0 until numCars
          car = Car(carNames(index), Tyre.SOFT, Driver(1,1), 200)
        yield car
        cars = l.toList

      def updateDisplayedCar(tyresType: String): Unit =
        context.view.updateDisplayedCar(currentCarIndex, tyresType)

      def displaySimulationPanel(): Unit =
        context.view.displaySimulationPanel()

  trait Interface extends Provider with Component:
    self: Requirements =>
