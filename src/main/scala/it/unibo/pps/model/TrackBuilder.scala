package it.unibo.pps.model

import alice.tuprolog.{Term, Theory}
import it.unibo.pps.view.simulation_panel.DrawingTurnParams
import it.unibo.pps.prolog.Scala2P
import it.unibo.pps.view.simulation_panel.{DrawingStraightParams, DrawingTurnParams}
import monix.eval.Task

given Conversion[String, Term] = Term.createTerm(_)
given Conversion[Seq[_], Term] = _.mkString("[", ",", "]")
given Conversion[String, Theory] = Theory.parseLazilyWithStandardOperators(_)
given Conversion[String, Int] = Integer.parseInt(_)

trait TrackBuilder:

  /** Method that loads a base track from a prolog file
    * @return
    *   a [[Track]]
    */
  def createBaseTrack(): Track

object TrackBuilder:

  def apply(): TrackBuilder =
    new TrackBuilderImpl()

  private class TrackBuilderImpl() extends TrackBuilder:

    private val engine = Scala2P.createEngine("/basetrack.pl")

    private def createStraight(l: List[String]): Sector.Straight =
      val d = DrawingStraightParams(
        (l(1), l(2)),
        (l(3), l(4)),
        (l(5), l(6)),
        (l(7), l(8))
      )
      Sector.Straight(l(0), d)

    private def loadStraights(): List[Sector.Straight] =
      val l = List("ID", "X0_E", "Y0_E", "X1_E", "Y1_E", "X0_I", "Y0_I", "X1_I", "Y1_I")
      val result = for
        s <- engine(
          "straight(id(ID), startPointE(X0_E, Y0_E), endPointE(X1_E, Y1_E), startPointI(X0_I, Y0_I), endPointI(X1_I, Y1_I))"
        )
        x = Scala2P.extractTermsToListOfStrings(s, l)
        straight = createStraight(x)
      yield straight
      result.toList

    private def createTurn(l: List[String]): Sector.Turn =
      val d = DrawingTurnParams(
        (l(1), l(2)),
        (l(3), l(4)),
        (l(5), l(6)),
        (l(7), l(8)),
        (l(9), l(10)),
        l(11)
      )
      Sector.Turn(l(0), d)

    private def loadTurns(): List[Sector.Turn] =
      val l = List("ID", "X", "Y", "X0_E", "Y0_E", "X0_I", "Y0_I", "X1_E", "Y1_E", "X1_I", "Y1_I", "D")
      val result = for
        s <- engine(
          "turn(id(ID), center(X, Y), startPointE(X0_E, Y0_E), startPointI(X0_I, Y0_I), endPointE(X1_E, Y1_E), endPointI(X1_I, Y1_I), direction(D))"
        )
        x = Scala2P.extractTermsToListOfStrings(s, l)
        turn = createTurn(x)
      yield turn
      result.toList

    override def createBaseTrack(): Track =
      val track = Track()
      loadStraights().foreach(track.addSector(_))
      loadTurns().foreach(track.addSector(_))
      track
