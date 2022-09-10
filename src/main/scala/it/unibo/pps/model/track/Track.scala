package it.unibo.pps.model.track

trait Track:
  def sectors: List[Sector]
  def startingGrid: List[StartingPoint]
  def addSector(sector: Sector): Unit
  def addStartingPoint(startingPoint: StartingPoint): Unit
  def nextSector(actualSector: Sector): Sector

object Track:
  def apply(): Track =
    new TrackImpl()

  private class TrackImpl() extends Track:

    private var _sectors: List[Sector] = List.empty
    private var _startingGrid: List[StartingPoint] = List.empty

    override def sectors: List[Sector] = _sectors
    override def addSector(sector: Sector): Unit =
      _sectors = _sectors :+ sector
    override def startingGrid: List[StartingPoint] = _startingGrid
    override def addStartingPoint(startingPoint: StartingPoint): Unit =
      _startingGrid = _startingGrid :+ startingPoint
    override def nextSector(actualSector: Sector): Sector =
      _sectors.filter(e => e.id == (((actualSector.id) % 4) + 1)).head
