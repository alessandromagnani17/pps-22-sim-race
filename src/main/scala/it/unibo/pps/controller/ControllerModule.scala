package it.unibo.pps.controller

import it.unibo.pps.engine.SimulationEngineModule
import it.unibo.pps.model.{Car, Driver, ModelModule, Snapshot, Standing, Tyre}
import it.unibo.pps.view.ViewModule
import it.unibo.pps.view.main_panel.ImageLoader
import monix.execution.Scheduler.Implicits.global
import monix.execution.Cancelable
import it.unibo.pps.utility.PimpScala.RichOption.*
import it.unibo.pps.view.simulation_panel.DrawingCarParams

import java.awt.Color
import scala.collection.mutable
import scala.collection.mutable.Map

object ControllerModule:
  trait Controller:
    def notifyStart(): Unit
    def notifyStop(): Unit
    //def notifyFinish(): Unit
    def notifyDecreseSpeed(): Unit
    def notifyIncreaseSpeed(): Unit
    def startingPositions: Map[Int, Car]
    def currentCar: Car
    def currentCarIndex: Int
    def standings: Standing
    def totalLaps: Int
    def currentCarIndex_=(index: Int): Unit
    def totalLaps_=(lap: Int): Unit
    def setPath(path: String): Unit
    def setTyre(tyre: Tyre): Unit
    def setMaxSpeed(speed: Int): Unit
    def setAttack(attack: Int): Unit
    def setDefense(defense: Int): Unit
    def displaySimulationPanel(): Unit
    def displayStartingPositionsPanel(): Unit
    def displayEndRacePanel(): Unit
    def updateParametersPanel(): Unit
    def updateDisplayedCar(): Unit
    def invertPosition(prevIndex: Int, nextIndex: Int): Unit

  trait Provider:
    val controller: Controller

  type Requirements = ModelModule.Provider with SimulationEngineModule.Provider with ViewModule.Provider

  trait Component:
    context: Requirements =>
    class ControllerImpl extends Controller:

      private val imageLoader = ImageLoader()
      private val numCars = 4
      private val carNames = List("Ferrari", "Mercedes", "Red Bull", "McLaren")
      private var stopFuture: Option[Cancelable] = None

      override def notifyStart(): Unit = stopFuture = Some(
        context.simulationEngine
          .simulationStep()
          .loopForever
          .runAsync {
            case Left(exp) => global.reportFailure(exp)
            case _ =>
          }
      )

      override def notifyStop(): Unit =
        stopFuture --> (_.cancel())
        stopFuture = None

      //override def notifyFinish(): Unit =
        //notifyStop()
        
        //displayEndRacePanel()

      override def notifyDecreseSpeed(): Unit =
        context.simulationEngine.decreaseSpeed()

      override def notifyIncreaseSpeed(): Unit =
        context.simulationEngine.increaseSpeed()

      override def startingPositions: mutable.Map[Int, Car] = context.model.startingPositions

      override def currentCar: Car = context.model.cars(context.model.currentCarIndex)

      override def currentCarIndex: Int = context.model.currentCarIndex

      override def totalLaps: Int = context.model.totalLaps

      override def standings: Standing = context.model.standing

      override def currentCarIndex_=(index: Int): Unit = context.model.currentCarIndex = index

      override def totalLaps_=(lap: Int): Unit = context.model.totalLaps_(lap)

      override def setPath(path: String): Unit = context.model.cars(context.model.currentCarIndex).path = path

      override def setTyre(tyre: Tyre): Unit = context.model.cars(context.model.currentCarIndex).tyre = tyre

      override def setMaxSpeed(speed: Int): Unit = context.model.cars(context.model.currentCarIndex).maxSpeed = speed

      override def setAttack(attack: Int): Unit = context.model.cars(context.model.currentCarIndex).driver.attack =
        attack

      override def setDefense(defense: Int): Unit = context.model.cars(context.model.currentCarIndex).driver.defense =
        defense

      override def displaySimulationPanel(): Unit =
        context.model.updateStanding()
        context.model.initSnapshot()
        context.view.updateDisplayedStanding()
        context.view.displaySimulationPanel(context.model.track, context.model.standing)
        context.view.updateCars(context.model.standing._standing)

      override def displayStartingPositionsPanel(): Unit =
        context.view.displayStartingPositionsPanel()

      override def displayEndRacePanel(): Unit =
        context.view.displayEndRacePanel()

      override def updateParametersPanel(): Unit =
        context.view.updateParametersPanel()

      override def updateDisplayedCar(): Unit =
        context.view.updateDisplayedCar()

      override def invertPosition(prevIndex: Int, nextIndex: Int): Unit =
        val car = context.model.startingPositions(prevIndex)
        context.model.startingPositions(prevIndex) = context.model.startingPositions(nextIndex)
        context.model.startingPositions(nextIndex) = car

        val position = context.model.startingPositions(prevIndex).drawingCarParams.position
        context.model.startingPositions(prevIndex).drawingCarParams.position =
          context.model.startingPositions(nextIndex).drawingCarParams.position
        context.model.startingPositions(nextIndex).drawingCarParams.position = position

  trait Interface extends Provider with Component:
    self: Requirements =>
