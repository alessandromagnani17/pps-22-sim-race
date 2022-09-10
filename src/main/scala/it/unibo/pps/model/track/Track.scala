package it.unibo.pps.model.track

import scala.{Tuple2 => Point2D}

trait Track:
  def sectors: List[Sector]
  def startingGrid: List[StartingPoint]
  def finalPositions: List[Point2D[Int, Int]]
  def addSector(sector: Sector): Unit
  def addStartingPoint(startingPoint: StartingPoint): Unit
  def addFinalPosition(finalPosition: Point2D[Int, Int]): Unit
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
