package it.unibo.pps.model

object ModelModule:
  trait Model:
    def _track: Track 
  
  trait Provider:
    val model: Model
  
  trait Component:
    class ModelImpl extends Model:
      
      val track = TrackBuilder().createBaseTrack()
      
      override def _track: Track = track
    
  trait Interface extends Provider with Component
  
  
