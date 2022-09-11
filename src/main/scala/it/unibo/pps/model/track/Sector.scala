package it.unibo.pps.model.track

import it.unibo.pps.model.*
import it.unibo.pps.utility.GivenConversion.DirectionGivenConversion.given
import it.unibo.pps.utility.PimpScala.RichInt.*

import scala.{Tuple2 => Point2d}

/** The direction of the sector */
enum Direction:
  case Forward
  case Backward

/** Represents the various phases of a sector indicating the actions that the pilot can carry out */
enum Phase:
  case Acceleration
  case Deceleration
  case Ended

/** A generic sector of the track */
sealed trait Sector:

  /** Returns the id of the sector */
  def id: Int

  /** Returns the render parameters of the sector */
  def renderParams: RenderParams

  /** Returns the direction of the sector */
  def direction: Direction

  /** Computes the phase given the car position
    * @param p
    *   The car position
    */
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
