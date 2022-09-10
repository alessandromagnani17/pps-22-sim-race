package it.unibo.pps.model.loader

import it.unibo.pps.model.track.{StartingPoint, Straight, Track, Turn}
import it.unibo.pps.model.{RenderStartingPointParams, RenderStraightParams, RenderTurnParams}
import it.unibo.pps.prolog.Scala2P
import it.unibo.pps.utility.GivenConversion.DirectionGivenConversion.given
import scala.{Tuple2 => Point2D}

class TrackLoader(theory: String) extends Loader:

  private val engine = Scala2P.createEngine(theory)

  override type E = Track

  /** Loads the track from the relative prolog file
    * @return
    *   [[Track]]
    */
  override def load: Track =
    val track = Track()
    loadStraights.foreach(track.addSector(_))
    loadTurns.foreach(track.addSector(_))
    loadStartingGrid.foreach(
      track.addStartingPoint(_)
    )
    loadFinalPositions.foreach(track.addFinalPosition(_))
    track

  private def loadStraights: List[Straight] =
    val l = List("ID", "X0_E", "Y0_E", "X1_E", "Y1_E", "X0_I", "Y0_I", "X1_I", "Y1_I", "END", "D")
    for
      s <- engine(
        "straight(id(ID), startPointE(X0_E, Y0_E), endPointE(X1_E, Y1_E), startPointI(X0_I, Y0_I), endPointI(X1_I, Y1_I), end(END), direction(D))"
      )
      x = Scala2P.extractTermsToListOfStrings(s, l)
      straight = mkStraight(x)
    yield straight

  private def loadTurns: List[Turn] =
    val l = List("ID", "X", "Y", "X0_E", "Y0_E", "X0_I", "Y0_I", "X1_E", "Y1_E", "X1_I", "Y1_I", "D", "TL", "BL")
    for
      s <- engine(
        "turn(id(ID), center(X, Y), startPointE(X0_E, Y0_E), startPointI(X0_I, Y0_I), endPointE(X1_E, Y1_E), endPointI(X1_I, Y1_I), direction(D), topLimit(TL), bottomLimit(BL))"
      )
      x = Scala2P.extractTermsToListOfStrings(s, l)
      turn = mkTurn(x)
    yield turn

  private def loadStartingGrid: List[StartingPoint] =
    val l = List("ID", "X_POSITION", "Y_POSITION")
    for
      s <- engine(
        "startingPoint(id(ID), position(X_POSITION, Y_POSITION))"
      )
      x = Scala2P.extractTermsToListOfStrings(s, l)
      startingPoint = mkStartingPoint(x)
    yield startingPoint

  private def loadFinalPositions: List[Point2D[Int, Int]] =
    val l = List("X", "Y")
    for
      s <- engine(
        "finalPosition(position(X, Y))"
      )
      x = Scala2P.extractTermsToListOfStrings(s, l)
      startingPoint = mkFinalPosition(x)
    yield startingPoint

  private def mkStraight(l: List[String]): Straight = l match
    case List(id, xP0Ex, yP0Ex, xP1Ex, yP1Ex, xP0In, yP0In, xP1In, yP1In, end, direction) =>
      val d = RenderStraightParams(
        (xP0Ex, yP0Ex),
        (xP1Ex, yP1Ex),
        (xP0In, yP0In),
        (xP1In, yP1In),
        end
      )
      Straight(id, direction, d)

  private def mkTurn(l: List[String]): Turn = l match
    case List(id, x_center, y_center, x_SP_E, y_SP_E, x_SP_I, y_SP_I, x_EP_E, y_EP_E, x_EP_I, y_EP_I, direction, tl,
          bl) =>
      val d = RenderTurnParams(
        (x_center, y_center),
        (x_SP_E, y_SP_E),
        (x_SP_I, y_SP_I),
        (x_EP_E, y_EP_E),
        (x_EP_I, y_EP_I),
        x_center,
        tl,
        bl
      )
      Turn(id, direction, d)

  private def mkStartingPoint(l: List[String]): StartingPoint = l match
    case List(id, x, y) => StartingPoint(id, RenderStartingPointParams((x, y)))

  private def mkFinalPosition(l: List[String]): Point2D[Int, Int] = l match
    case List(x, y) => (x.toInt, y.toInt)
