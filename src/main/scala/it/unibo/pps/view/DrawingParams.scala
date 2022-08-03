package it.unibo.pps.view

import scala.{Tuple2 => Point2d}

/*
trait DrawingStraightParams(p0: Point2d[Int, Int],
                            p1: Point2d[Int, Int],
                            p2: Point2d[Int, Int],
                            p3: Point2d[Int, Int])

object DrawingStraightParams:
  def apply(p0: Point2d[Int, Int], 
            p1: Point2d[Int, Int], 
            p2: Point2d[Int, Int], 
            p3: Point2d[Int, Int]): DrawingStraightParams =
    new DrawingStraightParamsImpl(p0, p1, p2, p3)
*/
case class DrawingStraightParams(p0: Point2d[Int, Int],
                                 p1: Point2d[Int, Int],
                                 p2: Point2d[Int, Int],
                                 p3: Point2d[Int, Int])


trait DrawingTurnParams

object DrawingTurnParams:
  def apply(center: Point2d[Int, Int],
            startPointE: Point2d[Int, Int],
            startPointI: Point2d[Int, Int],
            endPointE: Point2d[Int, Int],
            endPointI: Point2d[Int, Int],
            direction: Int
           ): DrawingTurnParams =
    new DrawingTurnParamsImpl(center, startPointE, startPointI, endPointE, endPointI, direction)

  private class DrawingTurnParamsImpl(center: Point2d[Int, Int],
                                      startPointE: Point2d[Int, Int],
                                      startPointI: Point2d[Int, Int],
                                      endPointE: Point2d[Int, Int],
                                      endPointI: Point2d[Int, Int],
                                      direction: Int) extends DrawingTurnParams