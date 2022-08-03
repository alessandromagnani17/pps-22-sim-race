package it.unibo.pps.model

import scala.{Tuple2 => Point2d}
import it.unibo.pps.util.PimpScala.RichInt._

enum Sector:
  case Straight(id: Int, initialX: Int, initialY: Int, finalX: Int, finalY: Int)
  case Turn(id: Int, center: Point2d[Int, Int], startPoint: Point2d[Int, Int], endPoint: Point2d[Int, Int])

object Sector:

  /** Method for computing the radius of a turn sector
    * @param t
    *   The specified turn of the track we want to know the radius of
    * @return
    *   The radius [:Int]
    */
  def radius(t: Turn): Int =
    ((t.startPoint._1 - t.center._1) ** 2 + (t.startPoint._2 - t.center._2) ** 2).root
