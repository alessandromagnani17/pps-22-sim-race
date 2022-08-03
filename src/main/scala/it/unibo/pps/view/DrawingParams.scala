package it.unibo.pps.view

import scala.{Tuple2 => Point2d}

case class DrawingStraightParams(
    p0: Point2d[Int, Int],
    p1: Point2d[Int, Int],
    p2: Point2d[Int, Int],
    p3: Point2d[Int, Int]
)

case class DrawingTurnParams(
    center: Point2d[Int, Int],
    startPointE: Point2d[Int, Int],
    startPointI: Point2d[Int, Int],
    endPointE: Point2d[Int, Int],
    endPointI: Point2d[Int, Int],
    direction: Int
)
