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
        
        /*
        Qui la mia idea era di mettere questo dentro al parametro di drawing params per andare a prendere le diverse coordinate,
        o comunque, invece di metterlo all'interno dei parametri, chiamare il foreach sulla lista di pitches (postazioni), 
        per arrivare alle diverse coordinate
        
        InitialPitch.listOfPitches. ...
        */
        
        Car("Ferrari", Tyre.SOFT, Driver(0, 0), 200, DrawingCarParams((453, 115), Color.CYAN)),
        Car("Mercedes", Tyre.SOFT, Driver(0, 0), 200, DrawingCarParams((473, 129), Color.RED)),
        Car("Red Bull", Tyre.SOFT, Driver(0, 0), 200, DrawingCarParams((493, 142), Color.BLUE)),
        Car("McLaren", Tyre.SOFT, Driver(0, 0), 200, DrawingCarParams((513, 155), Color.GREEN))
      )
      private val standig: Standing = Standing(cars)

      override def _cars: List[Car] = cars
      override def _track: Track = track
      override def _standing: Standing = standig

  trait Interface extends Provider with Component
