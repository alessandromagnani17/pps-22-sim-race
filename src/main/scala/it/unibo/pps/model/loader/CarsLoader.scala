package it.unibo.pps.model.loader

import alice.tuprolog.{Term, Theory}
import it.unibo.pps.model.{Car, CarColors, Driver, RenderCarParams, Track, Tyre}
import it.unibo.pps.prolog.Scala2P
import it.unibo.pps.utility.Constants.carsInitial
import java.awt.Color

given Itearable2List[E]: Conversion[Iterable[E], List[E]] = _.toList
given Conversion[String, Term] = Term.createTerm(_)
given Conversion[Seq[_], Term] = _.mkString("[", ",", "]")
given Conversion[String, Theory] = Theory.parseLazilyWithStandardOperators(_)
given Conversion[String, Int] = Integer.parseInt(_)

given Conversion[String, Tyre] = _ match
  case s: String if s.equals("Soft") => Tyre.SOFT
  case s: String if s.equals("Medium") => Tyre.MEDIUM
  case s: String if s.equals("Hard") => Tyre.HARD

given Conversion[String, Color] = _ match
  case s: String if s.equals("Ferrari") => CarColors.getColor(s)
  case s: String if s.equals("Mercedes") => CarColors.getColor(s)
  case s: String if s.equals("Red Bull") => CarColors.getColor(s)
  case s: String if s.equals("McLaren") => CarColors.getColor(s)

class CarsLoader(theory: String, track: Track) extends Loader:

  private val engine = Scala2P.createEngine(theory)

  override type E = List[Car]

  /** Loads the cars from the relative prolog file
    * @return
    *   [[List]] of [[Car]]
    */
  override def load: E =
    val variables =
      List("Path", "Name", "Tyre", "Skills", "MaxSpeed", "Acceleration", "ActualSector", "Fuel", "Color")
    for
      sol <- engine(
        "car(path(Path), name(Name), tyre(Tyre), driver(Skills), maxSpeed(MaxSpeed)," +
          "acceleration(Acceleration), actualSector(ActualSector), fuel(Fuel), color(Color))."
      )
      x = Scala2P.extractTermsToListOfStrings(sol, variables)
      car = mkCar(x, track)
    yield car

  private def mkCar(params: List[String], track: Track): Car = params match
    case List(path, name, tyre, skills, maxSpeed, acceleration, actualSector, fuel, carColor) =>
      val position = carsInitial(name)
      val startingPoint = track.startingGrid(position).renderParams.position
      Car(
        path,
        name,
        tyre,
        Driver(skills),
        maxSpeed,
        1,
        0,
        acceleration.toDouble,
        track.sectors.head,
        0,
        0,
        0,
        fuel.toDouble,
        1,
        RenderCarParams(startingPoint, carColor)
      )
