package it.unibo.pps.model

trait Track:
  def getSectors(): List[Sector]
  def addSector(sector: Sector): Unit

  //-----------------------------------------------------------------------------------------<<<<<<<<<<<<<<<<<<<<<<<<<
  def _startingPoints(): List[StartingPoint]
  def addStartingPoint(startingPoint: StartingPoint): Unit
//-----------------------------------------------------------------------------------------<<<<<<<<<<<<<<<<<<<<<<<<<

object Track:
  def apply(): Track =
    new TrackImpl()

  private class TrackImpl() extends Track:

    private var sectors: List[Sector] = List.empty
    private var startingPoints: List[StartingPoint] = List.empty

    override def getSectors(): List[Sector] = sectors
    override def addSector(sector: Sector): Unit =
      sectors = sectors :+ sector

    //-----------------------------------------------------------------------------------------<<<<<<<<<<<<<<<<<<<<<<<<<
    override def getPitches(): List[StartingPoint] = startingPoints
    override def addPitches(startingPoint: StartingPoint): Unit =
      startingPoints = startingPoints :+ startingPoint
//-----------------------------------------------------------------------------------------<<<<<<<<<<<<<<<<<<<<<<<<<
