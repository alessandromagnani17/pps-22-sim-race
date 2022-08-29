package it.unibo.pps.engine

import alice.tuprolog.{Term, Theory}
import it.unibo.pps.prolog.Scala2P
import it.unibo.pps.model.Car

given Conversion[String, Term] = Term.createTerm(_)
given Conversion[Seq[_], Term] = _.mkString("[", ",", "]")
given Conversion[String, Theory] = Theory.parseLazilyWithStandardOperators(_)

trait PrologMovements:
  def newPositionStraight(x: Int, velocity: Double, time: Int, acceleration: Double, i: Int): Int
  def newVelocityStraightAcc(car: Car, time: Int): Int
  def newVelocityStraightDec(car: Car, time: Int): Int

  def newPositionTurn(): Unit

object PrologMovements:
  def apply(): PrologMovements = new PrologMovementsImpl()

  private class PrologMovementsImpl() extends PrologMovements:

    private val engine = Scala2P.createEngine("/prolog/movements.pl")

    override def newPositionStraight(x: Int, velocity: Double, time: Int, acceleration: Double, i: Int): Int =
      engine(s"computeNewPositionForStraight($x, $velocity, $time, $acceleration, $i, Np)")
        .map(Scala2P.extractTermToString(_, "Np"))
        .toSeq
        .head
        .toDouble
        .toInt

    override def newVelocityStraightAcc(car: Car, time: Int): Int =
      engine(s"computeNewVelocity(${car.actualSpeed}, ${car.acceleration}, $time, ${car.degradation}, ${car.fuel}, Ns)")
        .map(Scala2P.extractTermToString(_, "Ns"))
        .toSeq
        .head
        .toDouble
        .toInt

    override def newVelocityStraightDec(car: Car, time: Int): Int =
      //println(s"${car.name} ----- maxspeed: ${car.maxSpeed} ----- speed: ${car.actualSpeed} ---- $car.")
      engine(s"computeNewVelocityDeceleration(${car.actualSpeed}, 1, $time, ${car.degradation}, ${car.fuel}, Ns)")
        .map(Scala2P.extractTermToString(_, "Ns"))
        .toSeq
        .head
        .toDouble
        .toInt

    override def newPositionTurn(): Unit = ???
