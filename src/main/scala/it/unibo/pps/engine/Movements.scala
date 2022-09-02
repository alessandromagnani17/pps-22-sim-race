package it.unibo.pps.engine

import alice.tuprolog.{Term, Theory}
import it.unibo.pps.prolog.Scala2P
import it.unibo.pps.model.Car
import it.unibo.pps.utility.monadic.io
import it.unibo.pps.view.simulation_panel.{DrawingParams, DrawingStraightParams, DrawingTurnParams}
import monix.eval.Task
import it.unibo.pps.utility.PimpScala.RichTuple2.*
import it.unibo.pps.utility.PimpScala.RichInt.*
import it.unibo.pps.model.Direction
import it.unibo.pps.given

trait Movements:
  def newVelocityStraightAcc(car: Car, time: Int): Int
  def newVelocityStraightDec(car: Car, time: Int): Int
  def acceleration(car: Car, time: Int): Task[(Int, Int)]
  def deceleration(car: Car, time: Int): Task[Tuple2[Int, Int]]
  def turn(car: Car, time: Int, velocity: Double, d: DrawingParams): Tuple2[Int, Int]

object Movements:
  def apply(): Movements = new MovementsImpl()

  private class MovementsImpl() extends Movements:

    private val engine = Scala2P.createEngine("/prolog/movements.pl")

    override def acceleration(car: Car, time: Int): Task[(Int, Int)] =
      for
        x <- io(car.drawingCarParams.position._1)
        i <- io(if car.actualSector.id == 1 then 1 else -1)
        velocity <- io(car.actualSpeed)
        acceleration <- io(car.acceleration)
        newP <- io(newPositionStraight(x, velocity, time, acceleration, i))
      yield (newP, car.drawingCarParams.position._2)

    override def deceleration(car: Car, time: Int): Task[Tuple2[Int, Int]] =
      for
        x <- io(car.drawingCarParams.position._1)
        i <- io(if car.actualSector.id == 1 then 1 else -1)
        newP <- io(newPositionStraight(x, car.actualSpeed, time, 1, i))
      yield (newP, car.drawingCarParams.position._2)

    override def turn(car: Car, time: Int, velocity: Double, d: DrawingParams): Tuple2[Int, Int] = d match
      case DrawingTurnParams(center, p, _, _, _, direction, endX) =>
        for
          x <- io(car.drawingCarParams.position._1)
          teta_t <- io(0.5 * car.acceleration * (time ** 2))
          r <- io(car.drawingCarParams.position euclideanDistance center)
          turnRadius <- io(center euclideanDistance p)
          alpha <- io(direction match
            case Direction.Forward => 0
            case Direction.Backward => 180
          )
          newX <- io((center._1 + (r * Math.sin(Math.toRadians(teta_t + alpha)))).toInt)
          newY <- io((center._2 - (r * Math.cos(Math.toRadians(teta_t + alpha)))).toInt)
          np <- io(checkBounds((newX, newY), center, turnRadius, direction))
          position <- io(checkEnd(np, endX, direction))
        yield position

    private def checkBounds(p: (Int, Int), center: (Int, Int), r: Int, direction: Int): (Int, Int) =
      var dx = (p._1 + 12, p._2) euclideanDistance center
      var dy = (p._1, p._2 + 12) euclideanDistance center
      val rI = 113
      if dx - r < 0 && direction == 1 then dx = r
      if dy - r < 0 && direction == 1 then dy = r
      if (dx >= r || dy >= r) && direction == 1 then (p._1 - (dx - r), p._2 - (dy - r))
      else if (dx <= rI || dy <= rI) && direction == -1 then (p._1 + (dx - rI), p._2 + (dy - rI))
      else p

    private def checkEnd(p: (Int, Int), end: Int, direction: Int): (Int, Int) =
      if direction == 1 then if p._1 < end then (end - 1, p._2) else p
      else if p._1 > end then (end + 1, p._2)
      else p

    private def newPositionStraight(x: Int, velocity: Double, time: Int, acceleration: Double, i: Int): Int =
      query(s"newPositionStraight($x, ${(velocity * 0.069).toInt}, $time, $acceleration, $i, Np)", "Np")

    override def newVelocityStraightAcc(car: Car, time: Int): Int =
      query(
        s"newVelocityAcceleration(${car.actualSpeed}, ${car.acceleration}, $time, ${car.degradation}, ${car.fuel}, Ns)",
        "Ns"
      )

    override def newVelocityStraightDec(car: Car, time: Int): Int =
      query(s"newVelocityDeceleration(${car.actualSpeed}, Ns)", "Ns")

    private def query(q: String, output: String): Int =
      engine(q)
        .map(Scala2P.extractTermToString(_, output))
        .toSeq
        .head
        .toDouble
        .toInt
