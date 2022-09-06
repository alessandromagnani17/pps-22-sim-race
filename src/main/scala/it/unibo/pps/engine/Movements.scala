package it.unibo.pps.engine

import alice.tuprolog.{Term, Theory}
import it.unibo.pps.prolog.Scala2P
import it.unibo.pps.model.{Car, Direction, Phase, RenderParams, RenderStraightParams, RenderTurnParams, Tyre}
import it.unibo.pps.utility.monadic.io
import monix.eval.Task
import it.unibo.pps.utility.PimpScala.RichTuple2.*
import it.unibo.pps.utility.PimpScala.RichInt.*
import it.unibo.pps.given

trait Movements:
  def updateVelocityStraight(car: Car, time: Int, phase: Phase): Int
  def updateVelocityTurn(car: Car): Int
  def updatePositionStraightAcceleration(car: Car, time: Int): Task[(Int, Int)]
  def updatePositionStraightDeceleration(car: Car, time: Int): Task[Tuple2[Int, Int]]
  def updatePositionTurn(car: Car, time: Int, velocity: Double, d: RenderParams): Tuple2[Int, Int]

object Movements:
  def apply(): Movements = new MovementsImpl()

  private class MovementsImpl() extends Movements:

    private val engine = Scala2P.createEngine("/prolog/movements.pl")

    override def updateVelocityStraight(car: Car, time: Int, phase: Phase): Int = phase match
      case Phase.Acceleration =>
        val v = updateVelocityStraightAcceleration(car, time)
        if v > car.maxSpeed then car.maxSpeed else v
      case Phase.Deceleration => updateVelocityStraightDeceleration(car, time)
      case Phase.Ended => car.actualSpeed

    override def updateVelocityTurn(car: Car): Int =
      (car.actualSpeed * (0.94 + (car.driver.skills / 100))).toInt

    override def updatePositionStraightAcceleration(car: Car, time: Int): Task[(Int, Int)] =
      for
        x <- io(car.renderCarParams.position._1)
        direction <- io(car.actualSector.direction)
        velocity <- io(car.actualSpeed)
        acceleration <- io(car.acceleration)
        newP <- io(newPositionStraight(x, velocity, time, acceleration, direction))
      yield (newP, car.renderCarParams.position._2)

    override def updatePositionStraightDeceleration(car: Car, time: Int): Task[Tuple2[Int, Int]] =
      for
        x <- io(car.renderCarParams.position._1)
        direction <- io(car.actualSector.direction)
        newP <- io(newPositionStraight(x, car.actualSpeed, time, 1, direction))
      yield (newP, car.renderCarParams.position._2)

    override def updatePositionTurn(car: Car, time: Int, velocity: Double, d: RenderParams): Tuple2[Int, Int] = d match
      case RenderTurnParams(center, p, _, _, _, endX) =>
        for
          x <- io(car.renderCarParams.position._1)
          teta_t <- io(0.5 * car.acceleration * (time ** 2))
          direction <- io(car.actualSector.direction)
          r <- io(car.renderCarParams.position euclideanDistance center)
          turnRadius <- io(center euclideanDistance p)
          alpha <- io(direction match
            case Direction.Forward => 0
            case Direction.Backward => 180
          )
          newX <- io((center._1 + (r * Math.sin(Math.toRadians(teta_t + alpha)))).toInt)
          newY <- io((center._2 - (r * Math.cos(Math.toRadians(teta_t + alpha)))).toInt)
          np <- io(checkTurnBounds((newX, newY), center, turnRadius, direction))
          position <- io(checkEnd(np, endX, direction))
        yield position

    private def checkTurnBounds(p: (Int, Int), center: (Int, Int), r: Int, direction: Int): (Int, Int) =
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

    private def newPositionStraight(x: Int, velocity: Double, time: Int, acceleration: Double, direction: Int): Int =
      query(s"newPositionStraight($x, ${(velocity * 0.069).toInt}, $time, $acceleration, $direction, Np)", "Np")

    private def calcLimitation(t: Tyre, lap: Int): Double = t match
      case Tyre.SOFT => Math.exp((2.0 / 11.0) * (lap / 10)) - 0.9
      case Tyre.MEDIUM => 0.4
      case Tyre.HARD => Math.exp((-1.0 / 3.0) * (lap / 10)) - 0.25

    private def updateVelocityStraightAcceleration(car: Car, time: Int): Int =
      val v = query(
        s"newVelocityAcceleration(${car.actualSpeed}, ${car.acceleration}, $time, ${car.degradation}, ${car.fuel}, Ns)",
        "Ns"
      )
      val offset: Int = ((v / 5) * calcLimitation(car.tyre, car.actualLap)).toInt
      v - offset

    private def updateVelocityStraightDeceleration(car: Car, time: Int): Int =
      query(s"newVelocityDeceleration(${car.actualSpeed}, Ns)", "Ns")

    private def query(q: String, output: String): Int =
      engine(q)
        .map(Scala2P.extractTermToString(_, output))
        .toSeq
        .head
        .toDouble
        .toInt
