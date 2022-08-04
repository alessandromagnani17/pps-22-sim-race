package it.unibo.pps.model

import it.unibo.pps.view.{DrawingStraightParams, DrawingTurnParams}

trait TrackBuilder:
  def createBaseTrack(w: Int, h: Int): Track

object TrackBuilder:

  def apply(): TrackBuilder =
    new TrackBuilderImpl()

  private class TrackBuilderImpl() extends TrackBuilder:

    private def computeStraightUp(w: Int, h: Int): DrawingStraightParams =
      val w1 = (0.3 * w).toInt
      val w2 = (0.7 * w).toInt
      val h1 = (0.3 * h).toInt
      val h3 = (0.4 * h).toInt
      DrawingStraightParams((w1, h1), (w2, h1), (w1, h3), (w2, h3))

    private def computeStraightDown(w: Int, h: Int): DrawingStraightParams =
      val w1 = (0.3 * w).toInt
      val w2 = (0.7 * w).toInt
      val h2 = (0.7 * h).toInt
      val h4 = (0.6 * h).toInt
      DrawingStraightParams((w1, h2), (w2, h2), (w1, h4), (w2, h4))

    private def computeTurnRight(w: Int, h: Int, direction: Int): DrawingTurnParams =
      var h1 = (0.3 * h).toInt
      var h2 = (0.7 * h).toInt
      val w2 = (0.7 * w).toInt
      val x0E = (0.7 * w).toInt
      val y0E = (h1 + h2) / 2
      val x1E = w2
      val y1E = h1
      val x2E = w2
      val y2E = h2
      h1 = (0.40 * h).toInt
      h2 = (0.60 * h).toInt
      val x1I = w2
      val x2I = w2
      val y1I = h1
      val y2I = h2
      DrawingTurnParams((x0E, y0E), (x1E, y1E), (x1I, y1I), (x2E, y2E), (x2I, y2I), direction)

    private def computeTurnLeft(w: Int, h: Int, direction: Int): DrawingTurnParams =
      val w1 = (0.3 * w).toInt
      var h1 = (0.3 * h).toInt
      var h2 = (0.7 * h).toInt
      val x0E = w1 //Da cambiare metto il riferimento a w1
      val y0E = (h1 + h2) / 2 //Da cambiare metto il riferimento a w1
      val x1E = w1
      val x2E = w1
      val y1E = h1
      val y2E = h2
      h1 = (0.40 * h).toInt
      h2 = (0.60 * h).toInt
      val x0I = w1 //Da cambiare metto il riferimento a w1
      val y0I = (h1 + h2) / 2 //Da cambiare metto il riferimento a w1
      val x1I = w1
      val x2I = w1
      val y1I = h1
      val y2I = h2
      DrawingTurnParams((x0E, y0E), (x1E, y1E), (x1I, y1I), (x2E, y2E), (x2I, y2I), direction)

    override def createBaseTrack(w: Int, h: Int): Track =
      val track = Track()
      track.addSector(Sector.Straight(1, computeStraightUp(w, h)))
      track.addSector(Sector.Turn(2, computeTurnRight(w, h, 1)))
      track.addSector(Sector.Straight(3, computeStraightDown(w, h)))
      track.addSector(Sector.Turn(4, computeTurnLeft(w, h, -1)))
      track
