package it.unibo.pps.model

import scala.Tuple2 as Point2d
import it.unibo.pps.utility.PimpScala.RichInt.*
import it.unibo.pps.view.simulation_panel.{DrawingStraightParams, DrawingTurnParams}

enum Sector:
  case Straight(id: Int, drawingParams: DrawingStraightParams)
  case Turn(id: Int, drawingParams: DrawingTurnParams)
