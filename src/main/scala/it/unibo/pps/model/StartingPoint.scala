package it.unibo.pps.model

import it.unibo.pps.view.simulation_panel.{DrawingCarParams, DrawingStartingPointParams}

/*enum InitialPitch:
  case listOfPitches(id: Int, drawingParams: DrawingCarParams)

 */

case class StartingPoint(id: Int, drawingParams: DrawingStartingPointParams)
