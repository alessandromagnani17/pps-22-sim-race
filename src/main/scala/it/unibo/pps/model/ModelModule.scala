package it.unibo.pps.model

object ModelModule:
  trait Model
  
  trait Provider:
    val model: Model
  
  trait Component:
    class ModelImpl extends Model
    
  trait Interface extends Provider with Component
