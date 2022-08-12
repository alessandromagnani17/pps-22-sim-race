package it.unibo.pps.controller

import it.unibo.pps.engine.SimulationEngineModule
import it.unibo.pps.model.{Car, Driver, ModelModule, Tyre}
import it.unibo.pps.view.ViewModule

object ControllerModule:
  trait Controller:
    def notifyStart(): Unit
    def createCars(): Unit
    def getCurrentCar(): Car
    def updateParametersPanel(): Unit
    def updateDisplayedCar(tyre: Tyre): Unit
    def getCurrentCarIndex(): Int
    def setCurrentCarIndex(index: Int): Unit
    def setTyre(tyre: Tyre): Unit
    def setMaxSpeed(speed: Int): Unit
    def setAttack(attack: Int): Unit
    def setDefense(defense: Int): Unit
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

      override def notifyStart(): Unit = ???

      override def getCurrentCarIndex(): Int = currentCarIndex

      override def setCurrentCarIndex(index: Int): Unit = currentCarIndex = index

      override def setTyre(tyre: Tyre): Unit = cars(currentCarIndex).tyre = tyre

      override def setMaxSpeed(speed: Int): Unit = cars(currentCarIndex).maxSpeed = speed

      override def setAttack(attack: Int): Unit = cars(currentCarIndex).driver.attack = attack

      override def setDefense(defense: Int): Unit = cars(currentCarIndex).driver.defense = defense

      override def getCurrentCar(): Car = cars(currentCarIndex)

      override def updateParametersPanel(): Unit =
        context.view.updateParametersPanel()

      override def createCars(): Unit =
        val l = for
          index <- 0 until numCars
          car = Car(carNames(index), Tyre.HARD, Driver(1,1), 200)
        yield car
        cars = l.toList

      override def updateDisplayedCar(tyre: Tyre): Unit =
        context.view.updateDisplayedCar(currentCarIndex, tyre)

      override def displaySimulationPanel(): Unit =
        context.view.displaySimulationPanel()

  trait Interface extends Provider with Component:
    self: Requirements =>
