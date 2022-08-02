package it.unibo.pps.model

trait Sector:
  def _id: Int
  
class Straight(id:Int, initialX: Int, initialY: Int, finalX: Int, finalY: Int) extends Sector:
  override def _id: Int = id
  
  
class Turn() extends Sector:
  override def _id: Int = ??? 
