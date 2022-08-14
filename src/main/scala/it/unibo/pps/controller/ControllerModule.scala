package it.unibo.pps.controller

import it.unibo.pps.engine.SimulationEngineModule
import it.unibo.pps.model.ModelModule
import it.unibo.pps.view.ViewModule
import monix.execution.Scheduler.Implicits.global
import monix.execution.Cancelable
import it.unibo.pps.utility.PimpScala.RichOption.*

object ControllerModule:
  trait Controller:
    def notifyStart(): Unit
    def notifyStop(): Unit
    def updateDisplayedCar(tyresType: String): Unit
    def setCurrentCarIndex(index: Int): Unit
    def displaySimulationPanel(): Unit
    def notifyDecreseSpeed(): Unit
    def notifyIncreaseSpeed(): Unit

  trait Provider:
    val controller: Controller

  type Requirements = ModelModule.Provider with SimulationEngineModule.Provider with ViewModule.Provider

  trait Component:
    context: Requirements =>
    class ControllerImpl extends Controller:
      private var currentCarIndex = 0
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

      override def notifyDecreseSpeed(): Unit =
        context.simulationEngine.decreaseSpeed()

      override def notifyIncreaseSpeed(): Unit =
        context.simulationEngine.increaseSpeed()

      def setCurrentCarIndex(index: Int): Unit = currentCarIndex = index

      def updateDisplayedCar(tyresType: String): Unit =
        context.view.updateDisplayedCar(currentCarIndex, tyresType)

      def displaySimulationPanel(): Unit =
        context.view.displaySimulationPanel(context.model.track, context.model.standing)
        context.view.updateCars(context.model.cars)

  trait Interface extends Provider with Component:
    self: Requirements =>
