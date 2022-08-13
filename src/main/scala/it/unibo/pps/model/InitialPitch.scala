package it.unibo.pps.model

import it.unibo.pps.view.DrawingCarParams

enum InitialPitch:
  case listOfPitches(id: Int, drawingParams: DrawingCarParams)
  
