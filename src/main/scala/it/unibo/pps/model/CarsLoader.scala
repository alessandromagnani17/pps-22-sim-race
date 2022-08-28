package it.unibo.pps.model

import alice.tuprolog.{Term, Theory}
import it.unibo.pps.prolog.Scala2P
import it.unibo.pps.view.simulation_panel.DrawingCarParams

import java.awt.Color

given Itearable2List[E]: Conversion[Iterable[E], List[E]] = _.toList
given Conversion[String, Term] = Term.createTerm(_)
given Conversion[Seq[_], Term] = _.mkString("[", ",", "]")
given Conversion[String, Theory] = Theory.parseLazilyWithStandardOperators(_)
given Conversion[String, Int] = Integer.parseInt(_)

given Conversion[String, Tyre] = _ match {
  case s: String if s.equals("Soft") => Tyre.SOFT
  case s: String if s.equals("Medium") => Tyre.MEDIUM
  case s: String if s.equals("Hard") => Tyre.HARD
}

object CarsLoader:

  private val engine = Scala2P.createEngine("")

  def load(track: Track): List[Car] =
    val variables = List("Path", "Name", "Tyre", "Attack", "Defense", "MaxSpeed", "ActualLap", "ActualSpeed",
      "Acceleration", "ActualSector", "Fuel")
    for
      sol <- engine(
        "car(path(Path), name(Name), tyre(Tyre), driver(Attack, Defense), maxSpeed(MaxSpeed), actualLap(ActualLap), " +
          "actualSpeed(ActualSpeed), acceleration(Acceleration), actualSector(ActualSector), fuel(Fuel))"
      )
      x = Scala2P.extractTermsToListOfStrings(sol, variables)
      car = mkCar(x, track)
    yield car

  private def mkCar(params: List[String], track: Track): Car = params match {
    case List(path, name, tyre, attack, defense, maxSpeed, actualLap, actualSpeed, acceleration, actualSector, fuel) =>
      Car(
        path,
        name,
        tyre,
        Driver(attack, defense),
        maxSpeed,
        actualLap,
        actualSpeed.toDouble,
        acceleration.toDouble,
        track.sectors.head,
        fuel.toDouble,
        DrawingCarParams((0, 0), Color.GREEN)
      )
  }
