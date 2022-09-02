package it.unibo.pps.model

import scala.{Tuple2 => Point2d}
import it.unibo.pps.utility.PimpScala.RichInt.*
import it.unibo.pps.view.simulation_panel.{DrawingParams, DrawingStraightParams, DrawingTurnParams}
import it.unibo.pps.given

enum Direction:
  case Forward
  case Backward

enum Phase:
  case Acceleration
  case Deceleration
  case Ended

sealed trait Sector:
  def id: Int
  def drawingParams: DrawingParams
  def phase(p: (Int, Int)): Phase

case class Straight(_id: Int, _drawingParams: DrawingStraightParams) extends Sector:
  override def id: Int = _id
  override def phase(p: (Int, Int)): Phase =
    val d = Math.abs(_drawingParams.endX - p._1)
    if d > 225 then Phase.Acceleration
    else if d > 0 then Phase.Deceleration
    else Phase.Ended
  override def drawingParams: DrawingParams = _drawingParams

case class Turn(_id: Int, _drawingParams: DrawingTurnParams) extends Sector:
  override def id: Int = _id
  override def phase(p: (Int, Int)): Phase =
    val d = (p._1 - _drawingParams.endX) * _drawingParams.direction
    if d >= 0 then Phase.Acceleration
    else Phase.Ended
  override def drawingParams: DrawingParams = _drawingParams
