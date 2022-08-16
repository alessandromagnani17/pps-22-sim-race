package it.unibo.pps.model

import it.unibo.pps.model.Car
import it.unibo.pps.view.simulation_panel.DrawingCarParams

import java.awt.Color

object ModelModule:
  trait Model:
    def track: Track
    def cars: List[Car]
    def standing: Standing

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
        Car("/cars/0-hard.png", "Ferrari", Tyre.SOFT, Driver(0, 0), 200, DrawingCarParams((453, 115), Color.CYAN)),
        Car("/cars/1-hard.png", "Mercedes", Tyre.SOFT, Driver(0, 0), 200, DrawingCarParams((473, 129), Color.RED)),
        Car("/cars/2-hard.png", "Red Bull", Tyre.SOFT, Driver(0, 0), 200, DrawingCarParams((493, 142), Color.BLUE)),
        Car("/cars/3-hard.png", "McLaren", Tyre.SOFT, Driver(0, 0), 200, DrawingCarParams((513, 155), Color.GREEN))
      )
      private val _standing: Standing = Standing(cars)

      override def cars: List[Car] = _cars
      override def track: Track = _track
      override def standing: Standing = _standing

  trait Interface extends Provider with Component
