package it.unibo.pps.model

import scala.Tuple2 as Point2d
import it.unibo.pps.util.PimpScala.RichInt.*
import it.unibo.pps.view.DrawingStraightParams
import it.unibo.pps.view.DrawingTurnParams

enum Sector:
  case Straight(id: Int, drawingParams: DrawingStraightParams)
  case Turn(id: Int, drawingParams: DrawingTurnParams)
