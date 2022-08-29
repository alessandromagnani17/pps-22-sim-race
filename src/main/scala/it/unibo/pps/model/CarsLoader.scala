package it.unibo.pps.model

import alice.tuprolog.{Term, Theory}
import it.unibo.pps.prolog.Scala2P
import it.unibo.pps.view.simulation_panel.DrawingCarParams
import java.awt.Color
import it.unibo.pps.utility.Constants.*

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

given Conversion[String, Color] = _ match {
  case s: String if s.equals("Red") => Color.RED
  case s: String if s.equals("Cyan") => Color.CYAN
  case s: String if s.equals("Blue") => Color.BLUE
  case s: String if s.equals("Green") => Color.GREEN
}

object CarsLoader:

  private val engine = Scala2P.createEngine("/prolog/cars.pl")

  def load(track: Track): List[Car] =
    val variables =
      List("Path", "Name", "Tyre", "Attack", "Defense", "MaxSpeed", "Acceleration", "ActualSector", "Fuel", "Color")
    for
      sol <- engine(
        "car(path(Path), name(Name), tyre(Tyre), driver(Attack, Defense), maxSpeed(MaxSpeed)," +
          "acceleration(Acceleration), actualSector(ActualSector), fuel(Fuel), color(Color))."
      )
      x = Scala2P.extractTermsToListOfStrings(sol, variables)
      car = mkCar(x, track)
    yield car

  private def mkCar(params: List[String], track: Track): Car = params match {
    case List(path, name, tyre, attack, defense, maxSpeed, acceleration, actualSector, fuel, carColor) =>
      val position = carsInitial(name)
      val startingPoint = track.startingGrid(position).drawingParams.position
      Car(
        path,
        name,
        tyre,
        Driver(attack, defense),
        maxSpeed,
        1,
        0,
        acceleration.toDouble,
        track.sectors.head,
        fuel.toDouble,
        0,
        DrawingCarParams(startingPoint, carColor)
      )
  }
