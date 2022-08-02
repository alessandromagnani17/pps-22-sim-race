package it.unibo.pps.model

trait Track:
  def getSectorByID(id: Int): Sector
  def addSector(sector: Sector): Unit

object Track:
  def apply(): Unit =
    new TrackImpl()

  private class TrackImpl() extends Track:

    private var sectors: List[Sector] = List.empty

    override def getSectorByID(id: Int): Sector =
      sectors.filter(_._id == id).head

    override def addSector(sector: Sector): Unit =
      sectors = sectors :+ sector


