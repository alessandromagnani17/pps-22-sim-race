package it.unibo.pps.model.loader

import alice.tuprolog.{Term, Theory}
import it.unibo.pps.model.car.{Car, CarColors, Tyre}
import it.unibo.pps.model.RenderCarParams
import it.unibo.pps.model.track.Track
import it.unibo.pps.prolog.Scala2P
import it.unibo.pps.model.car.Driver
import it.unibo.pps.engine.SimulationConstants.CAR_NAMES
import java.awt.Color
import it.unibo.pps.utility.GivenConversion.LoaderGivenConversion.given
import it.unibo.pps.utility.GivenConversion.CarLoaderGivenConversion.given

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
      val position = CAR_NAMES.filter((_, s) => name.equals(s)).toList.head._1
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
