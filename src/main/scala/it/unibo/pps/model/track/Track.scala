package it.unibo.pps.model.track

import scala.{Tuple2 => Point2D}

trait Track:

  /** Returns a [[List]] that contains all the track sectors */
  def sectors: List[Sector]

  /** Returns the starting grid */
  def startingGrid: List[StartingPoint]

  /** Returns the final positions */
  def finalPositions: List[Point2D[Int, Int]]

  /** Adds a sector to the track */
  def addSector(sector: Sector): Unit

  /** Adds a starting point to the track */
  def addStartingPoint(startingPoint: StartingPoint): Unit

  /** Adds a final position to the track */
  def addFinalPosition(finalPosition: Point2D[Int, Int]): Unit

  /** Computes the next sector of the track
    * @param actualSector
    *   The actual sector of the car
    * @return
    *   [[Sector]] The next sector
    */
  def nextSector(actualSector: Sector): Sector

object Track:
  def apply(): Track =
    new TrackImpl()

  private class TrackImpl() extends Track:

    private var _sectors: List[Sector] = List.empty
    private var _startingGrid: List[StartingPoint] = List.empty
    private var _finalPositions: List[Point2D[Int, Int]] = List.empty

    override def sectors: List[Sector] = _sectors

    override def finalPositions: List[(Int, Int)] = _finalPositions

    override def startingGrid: List[StartingPoint] = _startingGrid

    override def addSector(sector: Sector): Unit =
      _sectors = _sectors :+ sector

    override def addStartingPoint(startingPoint: StartingPoint): Unit =
      _startingGrid = _startingGrid :+ startingPoint

    override def addFinalPosition(finalPosition: (Int, Int)): Unit =
      _finalPositions = _finalPositions :+ finalPosition

    override def nextSector(actualSector: Sector): Sector =
      _sectors.filter(e => e.id == (((actualSector.id) % 4) + 1)).head
