package it.unibo.pps.model.track

import it.unibo.pps.model.*
import it.unibo.pps.utility.GivenConversion.DirectionGivenConversion.given
import it.unibo.pps.utility.PimpScala.RichInt.*

import scala.Tuple2 as Point2d

enum Direction:
  case Forward
  case Backward

enum Phase:
  case Acceleration
  case Deceleration
  case Ended

sealed trait Sector:
  def id: Int
  def renderParams: RenderParams
  def direction: Direction
  def phase(p: (Int, Int)): Phase

case class Straight(_id: Int, _direction: Direction, _drawingParams: RenderStraightParams) extends Sector:
  override def id: Int = _id
  override def direction: Direction = _direction
  override def phase(p: (Int, Int)): Phase =
    val d = Math.abs(_drawingParams.endX - p._1)
    if d > 225 then Phase.Acceleration
    else if d > 0 then Phase.Deceleration
    else Phase.Ended
  override def renderParams: RenderParams = _drawingParams

case class Turn(_id: Int, _direction: Direction, _drawingParams: RenderTurnParams) extends Sector:
  override def id: Int = _id
  override def direction: Direction = _direction
  override def phase(p: (Int, Int)): Phase =
    val d = (p._1 - _drawingParams.endX) * _direction
    if d >= 0 then Phase.Acceleration
    else Phase.Ended
  override def renderParams: RenderParams = _drawingParams
