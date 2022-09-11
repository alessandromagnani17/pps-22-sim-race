package it.unibo.pps

import alice.tuprolog.{Term, Theory}
import it.unibo.pps.model.{RenderParams, RenderTurnParams}
import it.unibo.pps.utility.PimpScala.RichTuple2.*
import it.unibo.pps.utility.PimpScala.RichInt.*
import it.unibo.pps.model.RenderTurnParams
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import scala.{Tuple2 => Point2D}

package object engine:

  /** Computes car radius when it is in a turn
    * @param d
    *   Turn render parameters
    * @param position
    *   Car position
    */
  val computeRadius = (d: RenderParams, position: Point2D[Int, Int]) =>
    d match
      case RenderTurnParams(center, _, _, _, _, _, _, _) => center euclideanDistance position

  /** Computes the angle between two points on a circumference using the cosine laws
    * @param a
    *   The first point
    * @param b
    *   The second point
    * @param radius
    *   The circumference radius
    */
  val angleBetweenPoints = (a: Point2D[Int, Int], b: Point2D[Int, Int], radius: Int) =>
    val distance = a euclideanDistance b
    Math.acos(((2 * radius ** 2) - distance) / (2 * radius ** 2))

  /** Computes the circular arc given radius and angle */
  val circularArc = (teta: Double, radius: Int) => (teta / 360) * 2 * radius * Math.PI

  given RunTask[E]: Conversion[Task[E], E] = _.runSyncUnsafe()
  given Itearable2List[E]: Conversion[Iterable[E], List[E]] = _.toList
  given Conversion[String, Term] = Term.createTerm(_)
  given Conversion[Seq[_], Term] = _.mkString("[", ",", "]")
  given Conversion[String, Theory] = Theory.parseLazilyWithStandardOperators(_)
