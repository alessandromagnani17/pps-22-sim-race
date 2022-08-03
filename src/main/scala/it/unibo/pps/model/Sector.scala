package it.unibo.pps.model

import scala.Tuple2 as Point2d
import it.unibo.pps.util.PimpScala.RichInt.*
import it.unibo.pps.view.DrawingStraightParams
import it.unibo.pps.view.DrawingTurnParams

enum Sector:
  case Straight(id: Int, drawingParams: DrawingStraightParams)
  case Turn(id: Int, drawingParams: DrawingTurnParams)

object Sector:

  /** Method for computing the radius of a turn sector
    * @param t
    *   The specified turn of the track we want to know the radius of
    * @return
    *   The radius [:Int]
    */
  def radius(t: Turn): Int = ???
    //((t.startPoint._1 - t.center._1) ** 2 + (t.startPoint._2 - t.center._2) ** 2).root
