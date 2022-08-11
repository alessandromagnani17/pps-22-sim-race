package it.unibo.pps.model

import it.unibo.pps.model.Car
import it.unibo.pps.view.DrawingCarParams

import java.awt.Color

object ModelModule:
  trait Model:
    def _track: Track
    def _cars: List[Car]
    def _standing: Standing

  trait Provider:
    val model: Model

  trait Component:
    class ModelImpl extends Model:

      private val track = TrackBuilder().createBaseTrack()
      private val cars = List(
        Car("Ferrari", Tyre.SOFT, Driver(0, 0), 200, DrawingCarParams((100, 100), Color.CYAN)),
        Car("Mercedes", Tyre.SOFT, Driver(0, 0), 200, DrawingCarParams((100, 200), Color.RED)),
        Car("Red Bull", Tyre.SOFT, Driver(0, 0), 200, DrawingCarParams((100, 300), Color.BLUE)),
        Car("McLaren", Tyre.SOFT, Driver(0, 0), 200, DrawingCarParams((100, 400), Color.GREEN))
      )
      private val standig: Standing = Standing(cars)

      override def _cars: List[Car] = cars
      override def _track: Track = track
      override def _standing: Standing = standig

  trait Interface extends Provider with Component
