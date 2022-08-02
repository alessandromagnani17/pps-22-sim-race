package it.unibo.pps.model

trait Track:
  def getSectorByID(id: Int): Option[Sector]
  def addSector(sector: Sector): Unit

object Track:
  def apply(): Track =
    new TrackImpl()

  private class TrackImpl() extends Track:

    private var sectors: List[Sector] = List.empty

    override def getSectorByID(id: Int): Option[Sector] =
      sectors.filter(_._id == id).headOption

    override def addSector(sector: Sector): Unit =
      sectors = sectors :+ sector
