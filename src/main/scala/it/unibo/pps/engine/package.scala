package it.unibo.pps

import it.unibo.pps.utility.PimpScala.RichTuple2.*
import it.unibo.pps.utility.PimpScala.RichInt.*
import it.unibo.pps.view.simulation_panel.{DrawingTurnParams, DrawingParams}

package object engine:

  val computeRadius = (d: DrawingParams, position: Tuple2[Int, Int]) =>
    d match
      case DrawingTurnParams(center, _, _, _, _, _, _) => center euclideanDistance position

  val angleBetweenPoints = (a: Tuple2[Int, Int], b: Tuple2[Int, Int], radius: Int) =>
    val distance = a euclideanDistance b
    Math.acos(((2 * radius ** 2) - distance) / (2 * radius ** 2))

  val circularArc = (teta: Double, radius: Int) => (teta / 360) * 2 * radius * Math.PI
