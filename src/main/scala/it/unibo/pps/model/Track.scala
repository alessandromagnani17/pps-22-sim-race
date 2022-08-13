package it.unibo.pps.model

trait Track:
  def getSectors(): List[Sector]
  def addSector(sector: Sector): Unit
  
  //-----------------------------------------------------------------------------------------<<<<<<<<<<<<<<<<<<<<<<<<< 
  def getPitches(): List[InitialPitch]
  def addPitches(pitches: InitialPitch):Unit
  //-----------------------------------------------------------------------------------------<<<<<<<<<<<<<<<<<<<<<<<<<

object Track:
  def apply(): Track =
    new TrackImpl()

  private class TrackImpl() extends Track:

    private var sectors: List[Sector] = List.empty
    private var pitches: List[InitialPitch] = List.empty

    override def getSectors(): List[Sector] = sectors
    override def addSector(sector: Sector): Unit =
      sectors = sectors :+ sector

    //-----------------------------------------------------------------------------------------<<<<<<<<<<<<<<<<<<<<<<<<<
    override def getPitches(): List[InitialPitch] = pitches
    override def addPitches(pitch: InitialPitch): Unit =
      pitches = pitches :+ pitch
    //-----------------------------------------------------------------------------------------<<<<<<<<<<<<<<<<<<<<<<<<<
    