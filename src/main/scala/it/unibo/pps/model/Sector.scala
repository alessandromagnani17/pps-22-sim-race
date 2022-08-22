package it.unibo.pps.model

import scala.Tuple2 as Point2d
import it.unibo.pps.utility.PimpScala.RichInt.*
import it.unibo.pps.view.simulation_panel.{DrawingStraightParams, DrawingTurnParams}

enum Phase:
  case Acceleration
  case Deceleration
  case Ended

sealed trait Sector:
  def id: Int
  def phase(p: (Int, Int)): Phase

case class Straight(_id: Int, drawingParams: DrawingStraightParams) extends Sector:
  override def id: Int = _id
  override def phase(p: (Int, Int)): Phase =
    val d = drawingParams.p1External._1 - p._1
    if d > 225 then Phase.Acceleration
    else if d > 0 then Phase.Deceleration
    else Phase.Ended

case class Turn(_id: Int, drawingParams: DrawingTurnParams) extends Sector:
  override def id: Int = _id
  override def phase(p: (Int, Int)): Phase = Phase.Ended
