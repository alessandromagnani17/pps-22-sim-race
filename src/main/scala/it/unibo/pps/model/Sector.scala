package it.unibo.pps.model

import scala.Tuple2 as Point2d
import it.unibo.pps.utility.PimpScala.RichInt.*
import it.unibo.pps.view.simulation_panel.{DrawingStraightParams, DrawingTurnParams}

/*enum Sector():
  case Straight(id: Int, drawingParams: DrawingStraightParams)
  case Turn(id: Int, drawingParams: DrawingTurnParams)*/

trait Sector:
  def id: Int

case class Straight(_id: Int, drawingParams: DrawingStraightParams) extends Sector:
  override def id: Int = _id

case class Turn(_id: Int, drawingParams: DrawingTurnParams) extends Sector:
  override def id: Int = _id