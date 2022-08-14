package it.unibo.pps.model

trait Track:
  def _sectors(): List[Sector]
  def addSector(sector: Sector): Unit
  def _startingGrid: List[StartingPoint]
  def addStartingPoint(startingPoint: StartingPoint): Unit

object Track:
  def apply(): Track =
    new TrackImpl()

  private class TrackImpl() extends Track:

    private var sectors: List[Sector] = List.empty
    private var startingGrid: List[StartingPoint] = List.empty

    override def _sectors(): List[Sector] = sectors
    override def addSector(sector: Sector): Unit =
      sectors = sectors :+ sector
    override def _startingGrid: List[StartingPoint] = startingGrid
    override def addStartingPoint(startingPoint: StartingPoint): Unit =
      startingGrid = startingGrid :+ startingPoint
