package it.unibo.pps.model

trait Track:
  def getSectors(): List[Sector]
  def addSector(sector: Sector): Unit

object Track:
  def apply(): Track =
    new TrackImpl()

  private class TrackImpl() extends Track:

    private var sectors: List[Sector] = List.empty

    override def getSectors(): List[Sector] = sectors
    override def addSector(sector: Sector): Unit =
      sectors = sectors :+ sector
    
    