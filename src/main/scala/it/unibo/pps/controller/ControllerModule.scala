package it.unibo.pps.controller

import it.unibo.pps.engine.SimulationEngineModule
import it.unibo.pps.model.car.{Car, Tyre}
import it.unibo.pps.model.{ModelModule, RenderCarParams, Snapshot, Standings}
import it.unibo.pps.view.ViewModule
import monix.execution.Scheduler.Implicits.global
import monix.execution.{Ack, Cancelable, contravariantCallback}
import it.unibo.pps.utility.PimpScala.RichOption.*
import it.unibo.pps.utility.UtilityFunctions
import monix.eval.Task

import java.awt.Color
import scala.collection.mutable
import scala.collection.mutable.Map
import scala.math.BigDecimal

object ControllerModule:
  trait Controller:

    /** Starts the simulation */
    def notifyStart: Unit

    /** Stops the simulation */
    def notifyStop: Unit

    /** Decreases simulation speed */
    def notifyDecreaseSpeed: Unit

    /** Increases simulation speed */
    def notifyIncreaseSpeed: Unit

    /** Restarts the whole simulator */
    def startNewSimulation: Unit

    /** Returns the cars of the simulation */
    def cars: List[Car]

    /** Returns the starting positions of the race */
    def startingPositions: List[Car]

    /** Returns the current car displayed in CarSelectionPanel */
    def currentCar: Car

    /** Returns the index of the current car displayed in CarSelectionPanel */
    def currentCarIndex: Int

    /** Returns the current standings of the race */
    def standings: Standings

    /** Returns the total number of laps */
    def totalLaps: Int

    /** Returns the fastest lap of the race */
    def fastestLap: Int

    /** Returns the name of the car that has made the fastest lap */
    def fastestCar: String

    /** Method that updates the current car index
      * @param index
      *   The new car index
      */
    def currentCarIndex_=(index: Int): Unit

    /** Method that updates the number of laps
      * @param lap
      *   The new number of laps
      */
    def totalLaps_=(lap: Int): Unit

    /** Method that updates the fastest lap
      * @param lap
      *   The new fastest lap
      */
    def fastestLap_=(lap: Int): Unit

    /** Method that updates the car that has made the fastest lap
      * @param carName
      *   The name of the car
      */
    def fastestCar_=(carName: String): Unit

    /** Method that updates the path of the car displayed in CarSelectionPanel after a tyre's type modification
      * @param path
      *   The new path
      */
    def setPath(path: String): Unit

    /** Method that updates the type of the tyres of the displayed car
      * @param tyre
      *   The new tyres type
      */
    def setTyre(tyre: Tyre): Unit

    /** Method that updates the maximum speed of the car displayed in CarSelectionPanel
      * @param speed
      *   The new maximum speed
      */
    def setMaxSpeed(speed: Int): Unit

    /** Method that updates the skills of the car displayed in CarSelectionPanel
      * @param skills
      *   The new skills
      */
    def setSkills(skills: Int): Unit

    /** Method that displays the SimulationPanel */
    def displaySimulationPanel: Unit

    /** Method that displays the StartingPositionsPanel */
    def displayStartingPositionsPanel: Unit

    /** Method that displays the EndRacePanel */
    def displayEndRacePanel: Unit

    /** Method that updates the displayed parameters when the car displayed is changed */
    def updateParametersPanel: Unit

    /** Method that updates the car displayed */
    def updateDisplayedCar: Unit

    /** Method that inverts the starting positions of two cars
      * @param prevIndex
      *   The index of the previous car
      * @param nextIndex
      *   The index of the next car
      */
    def invertPosition(prevIndex: Int, nextIndex: Int): Unit

    /** Registers necessary callbacks for reactive charts */
    def registerReactiveChartCallback: Unit

    /** Method that updates the index of the current displayed car
      * @param calcIndex
      *   The strategy applied to the current index
      */
    def updateCurrentCarIndex(calcIndex: Int => String): Unit

  trait Provider:
    val controller: Controller

  type Requirements = ModelModule.Provider with SimulationEngineModule.Provider with ViewModule.Provider

  trait Component:
    context: Requirements =>
    class ControllerImpl extends Controller:

      private var stopFuture: Option[Cancelable] = None

      override def notifyStart: Unit = stopFuture = Some(
        context.simulationEngine.simulationStep.loopForever
          .runAsync {
            case Left(exp) => global.reportFailure(exp)
            case _ =>
          }
      )

      override def notifyStop: Unit =
        stopFuture --> (_.cancel())
        stopFuture = None

      override def notifyDecreaseSpeed: Unit =
        context.simulationEngine.decreaseSpeed

      override def notifyIncreaseSpeed: Unit =
        context.simulationEngine.increaseSpeed

      override def cars: List[Car] = context.model.cars

      override def startingPositions: List[Car] = context.model.startingPositions

      override def currentCar: Car = context.model.cars(context.model.currentCarIndex)

      override def currentCarIndex: Int = context.model.currentCarIndex

      override def standings: Standings = context.model.standings

      override def totalLaps: Int = context.model.totalLaps

      override def fastestLap: Int = context.model.fastestLap

      override def fastestCar: String = context.model.fastestCar

      override def currentCarIndex_=(index: Int): Unit = context.model.currentCarIndex = index

      override def totalLaps_=(lap: Int): Unit = context.model.totalLaps_(lap)

      override def fastestLap_=(lap: Int): Unit = context.model.fastestLap = lap

      override def fastestCar_=(carName: String): Unit = context.model.fastestCar = carName

      override def setPath(path: String): Unit = context.model.cars(context.model.currentCarIndex).path = path

      override def setTyre(tyre: Tyre): Unit = context.model.cars(context.model.currentCarIndex).tyre = tyre

      override def setMaxSpeed(speed: Int): Unit = context.model.cars(context.model.currentCarIndex).maxSpeed = speed

      override def setSkills(skills: Int): Unit = context.model.cars(context.model.currentCarIndex).driver.skills =
        skills

      override def displaySimulationPanel: Unit =
        context.model.createStandings
        context.model.initSnapshot
        context.view.updateDisplayedStandings
        context.view.displaySimulationPanel(
          context.model.track,
          context.model.cars,
          context.model.actualLap,
          context.model.totalLaps
        )

      override def displayStartingPositionsPanel: Unit =
        context.view.displayStartingPositionsPanel

      override def displayEndRacePanel: Unit =
        context.view.displayEndRacePanel

      override def updateParametersPanel: Unit =
        context.view.updateParametersPanel

      override def updateDisplayedCar: Unit =
        context.view.updateDisplayedCar

      override def startNewSimulation: Unit =
        context.model.resetModel
        context.view.resetView
        context.simulationEngine.resetEngine

      override def registerReactiveChartCallback: Unit =
        val onNext = (l: List[Snapshot]) =>
          context.view.updateCharts(l)
          Ack.Continue
        val onError = (t: Throwable) => ()
        val onComplete = () => ()
        context.model.registerCallbackHistory(onNext, onError, onComplete)

      override def invertPosition(prevIndex: Int, nextIndex: Int): Unit =
        val car: Car = context.model.startingPositions(prevIndex)
        context.model.startingPositions =
          context.model.startingPositions.updated(prevIndex, context.model.startingPositions(nextIndex))
        context.model.startingPositions = context.model.startingPositions.updated(nextIndex, car)

        val position = context.model.startingPositions(prevIndex).renderCarParams.position
        context.model.startingPositions(prevIndex).renderCarParams.position =
          context.model.startingPositions(nextIndex).renderCarParams.position
        context.model.startingPositions(nextIndex).renderCarParams.position = position

      override def updateCurrentCarIndex(calcIndex: Int => String): Unit =
        val nextIndex = calcIndex(currentCarIndex)
        currentCarIndex = nextIndex.toInt
        currentCar.path = s"/cars/$nextIndex-${currentCar.tyre.toString.toLowerCase}.png"

  trait Interface extends Provider with Component:
    self: Requirements =>
