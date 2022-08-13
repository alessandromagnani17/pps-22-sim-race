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
        Se provi a runnare il programma, in console si vede la stampa che ho messo dentro createBaseTruck(), dove si vede
        che la lettura da file viene fatta bene.
        La mia idea era poi di mettere qui sotto, magari dentro a drawingCarParams, qualcosa come:
       
        track.getPitches().apply(0) 
        
        In modo prendere i valori di ogni postazione, per le 4 postazioni
          
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
