package it.unibo.pps.view

import scala.{Tuple2 => Point2d}
import java.awt.Color

sealed trait DrawingParams

/** Params needed to render a Straight sector: a straight is composed by two lines, one external and one internal
  * @param p0External
  *   Top left point2d of the straight
  * @param p1External
  *   Top right point2d of the straight
  * @param p0Internal
  *   Bottom left point2d of the straight
  * @param p1Internal
  *   Bottom right point2d of the straight
  */
case class DrawingStraightParams(
    p0External: Point2d[Int, Int],
    p1External: Point2d[Int, Int],
    p0Internal: Point2d[Int, Int],
    p1Internal: Point2d[Int, Int]
) extends DrawingParams

/** Params needed to render a Turn sector: a turn is composed by two concentrics circumference arcs, one external and
  * one internal
  * @param center
  *   The center of the two concentrics circumference arcs
  * @param startPointE
  *   Top point2d of the external circumference
  * @param startPointI
  *   Top point2d of the internal circumference
  * @param endPointE
  *   Bottom point2d of the external circumference
  * @param endPointI
  *   Bottom point2d of the internal circumference
  */
case class DrawingTurnParams(
    center: Point2d[Int, Int],
    startPointE: Point2d[Int, Int],
    startPointI: Point2d[Int, Int],
    endPointE: Point2d[Int, Int],
    endPointI: Point2d[Int, Int],
    direction: Int
) extends DrawingParams

/** Parames needed to render a car
  *
  * @param position
  *   The position of the car
  * @param color
  *   The color of the car
  */
case class DrawingCarParams(
    position: Point2d[Int, Int],
    color: Color
) extends DrawingParams

/** Parames needed to render a car
  *
  * @param position
  *   The position of the car
  */
case class DrawingStartingPointParams(position: Point2d[Int, Int]) extends DrawingParams
