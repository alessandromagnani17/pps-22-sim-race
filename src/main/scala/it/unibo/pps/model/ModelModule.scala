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
        Car("Ferrari", Tyre.SOFT, Driver(0, 0), 200, DrawingCarParams((453, 115), Color.CYAN)),
        Car("Mercedes", Tyre.SOFT, Driver(0, 0), 200, DrawingCarParams((468, 129), Color.RED)),
        Car("Red Bull", Tyre.SOFT, Driver(0, 0), 200, DrawingCarParams((483, 142), Color.BLUE)),
        Car("McLaren", Tyre.SOFT, Driver(0, 0), 200, DrawingCarParams((498, 155), Color.GREEN))
      )
      private val standig: Standing = Standing(cars)

      override def _cars: List[Car] = cars
      override def _track: Track = track
      override def _standing: Standing = standig

  trait Interface extends Provider with Component
