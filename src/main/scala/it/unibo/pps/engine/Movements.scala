package it.unibo.pps.engine

import alice.tuprolog.{Term, Theory}
import it.unibo.pps.prolog.Scala2P
import it.unibo.pps.model.{Car, Direction, Phase, RenderParams, RenderStraightParams, RenderTurnParams, Tyre}
import it.unibo.pps.utility.monadic.io
import monix.eval.Task
import it.unibo.pps.utility.PimpScala.RichTuple2.*
import it.unibo.pps.utility.PimpScala.RichInt.*
import it.unibo.pps.given
import it.unibo.pps.model.factor.CarFactorsManager

object Converter:
  def kmh2ms(vel: Double): Double = vel / 3.6
  def ms2kmh(vel: Double): Double = vel * 3.6

trait Movements:
  def updateVelocityStraight(car: Car, time: Int, phase: Phase): Task[Int]
  def updateVelocityTurn(car: Car): Task[Int]
  def updatePositionStraightAcceleration(car: Car, time: Int): Task[(Int, Int)]
  def updatePositionStraightDeceleration(car: Car, time: Int): Task[Tuple2[Int, Int]]
  def updatePositionTurn(car: Car, time: Int, velocity: Double, d: RenderParams): Tuple2[Int, Int]

object Movements:
  def apply(): Movements = new MovementsImpl()

  private class MovementsImpl() extends Movements:

    override def updateVelocityStraight(car: Car, time: Int, phase: Phase): Task[Int] = phase match
      case Phase.Acceleration => updateVelocityStraightAcceleration(car, time)
      case Phase.Deceleration => updateVelocityStraightDeceleration(car, time)
      case Phase.Ended => io(car.actualSpeed)

    override def updateVelocityTurn(car: Car): Task[Int] =
      io((car.actualSpeed * (0.94 + (car.driver.skills / 100))).toInt)

    override def updatePositionStraightAcceleration(car: Car, time: Int): Task[(Int, Int)] =
      for
        x <- io(car.renderCarParams.position._1)
        direction <- io(car.actualSector.direction)
        velocity <- io(car.actualSpeed)
        acceleration <- io(car.acceleration)
        newP <- newPositionStraight(x, velocity, time, acceleration, direction)
      yield (newP, car.renderCarParams.position._2)

    override def updatePositionStraightDeceleration(car: Car, time: Int): Task[Tuple2[Int, Int]] =
      for
        x <- io(car.renderCarParams.position._1)
        direction <- io(car.actualSector.direction)
        newP <- newPositionStraight(x, car.actualSpeed, time, 1, direction)
      yield (newP, car.renderCarParams.position._2)

    override def updatePositionTurn(car: Car, time: Int, velocity: Double, d: RenderParams): Tuple2[Int, Int] = d match
      case RenderTurnParams(center, pExternal, pInternal, _, _, endX) =>
        for
          x <- io(car.renderCarParams.position._1)
          teta_t <- io(0.5 * car.acceleration * (time ** 2))
          direction <- io(car.actualSector.direction)
          r <- io(car.renderCarParams.position euclideanDistance center)
          turnRadiusExternal <- io(center euclideanDistance pExternal)
          turnRadiusInternal <- io(center euclideanDistance pInternal)
          alpha <- io(direction match
            case Direction.Forward => 0
            case Direction.Backward => 180
          )
          newX <- io((center._1 + (r * Math.sin(Math.toRadians(teta_t + alpha)))).toInt)
          newY <- io((center._2 - (r * Math.cos(Math.toRadians(teta_t + alpha)))).toInt)
          np <- io(checkTurnBounds((newX, newY), center, turnRadiusExternal, turnRadiusInternal, direction))
          position <- io(checkEnd(np, endX, direction))
        yield position

    private def checkTurnBounds(p: (Int, Int), center: (Int, Int), rExternal: Int, rInternal: Int, direction: Int): (
        Int,
        Int
    ) =
      var dx = (p._1 + 12, p._2) euclideanDistance center
      var dy = (p._1, p._2 + 12) euclideanDistance center
      if dx - rExternal < 0 && direction == 1 then dx = rExternal
      if dy - rExternal < 0 && direction == 1 then dy = rExternal
      if (dx >= rExternal || dy >= rExternal) && direction == 1 then (p._1 - (dx - rExternal), p._2 - (dy - rExternal))
      else if (dx <= rInternal || dy <= rInternal) && direction == -1 then
        (p._1 + (dx - rInternal), p._2 + (dy - rInternal))
      else p

    private def checkEnd(p: (Int, Int), end: Int, direction: Int): (Int, Int) =
      if direction == 1 then if p._1 < end then (end - 1, p._2) else p
      else if p._1 > end then (end + 1, p._2)
      else p

    private def newPositionStraight(
        x: Int,
        velocity: Double,
        time: Int,
        acceleration: Double,
        direction: Int
    ): Task[Int] =
      for
        v <- io(Converter.kmh2ms(velocity))
        vel <- io((x + ((v * time + 0.5 * acceleration * (time ** 2)) / 160) * direction).toInt)
      yield vel

    private def updateVelocityStraightAcceleration(car: Car, time: Int): Task[Int] =
      for
        vel <- io(Converter.kmh2ms(car.actualSpeed))
        v <- io((vel + car.acceleration * time).toInt)
        v <- io(Converter.ms2kmh(v).toInt)
        v <- io(if v > car.maxSpeed then car.maxSpeed else v)
        d <- io(CarFactorsManager.totalDamage(v, car.fuel, (car.tyre, car.actualLap), car.degradation))
        v <- io(v - d)
      yield v

    private def updateVelocityStraightDeceleration(car: Car, time: Int): Task[Int] = io((car.actualSpeed * 0.95).toInt)
