package it.unibo.pps.view.simulation_panel

import it.unibo.pps.model.{Sector, Track}
import java.awt.{Color, Dimension, Graphics}
import javax.swing.JPanel
import it.unibo.pps.utility.PimpScala.RichTuple2._
import scala.{Tuple2 => Point2d}
import it.unibo.pps.view.ViewConstants.*
import it.unibo.pps.model.{Car, Straight, Turn}

class Environment(val w: Int, val h: Int) extends JPanel:

  private var _track: Track = Track()
  private var _cars: List[Car] = List.empty
  private var _actualLap: Int = 1
  private var _totalLaps: Int = 20

  def track: Track = _track
  def track_=(t: Track): Unit = _track = t
  def cars: List[Car] = _cars
  def cars_=(c: List[Car]): Unit = _cars = c
  def actualLap: Int = _actualLap
  def actualLap_=(actualLap: Int) = _actualLap = actualLap
  def totalLaps: Int = _totalLaps
  def totalLaps_=(totalLaps: Int) = _totalLaps = totalLaps

  override def getPreferredSize: Dimension = new Dimension(w, h)
  override def paintComponent(g: Graphics): Unit =
    super.paintComponent(g)
    g.setColor(Color.BLACK)
    g.drawString(s"LAP: ${ if _actualLap > _totalLaps then _totalLaps else _actualLap} / $_totalLaps", 449, 60)
    g.drawString("4° ", 303, 283)
    g.drawString("3° ", 403, 283)
    g.drawString("2° ", 503, 283)
    g.drawString("1° ", 603, 283)

    g.drawLine(230, 113, 230, 170)

    _cars.foreach(c => drawCar(c.drawingCarParams.position, c.drawingCarParams.color, g))

    g.setColor(Color.BLACK)

    def sketcher(e: Sector) = e match {
      case s: Straight => drawStraigth(s, g)
      case t: Turn => drawTurn(t, g)
    }

    _track.sectors.foreach(sketcher(_))
    g.drawRect(0, 0, w, h)

  private def drawStraigth(s: Straight, g: Graphics): Unit = s.drawingParams match {
    case DrawingStraightParams(p0External, p1External, p0Internal, p1Internal, _) =>
      g.drawLine(p0External._1, p0External._2, p1External._1, p1External._2)
      g.drawLine(p0Internal._1, p0Internal._2, p1Internal._1, p1Internal._2)
  }

  private def drawTurn(t: Turn, g: Graphics): Unit = t.drawingParams match {
    case DrawingTurnParams(center, startPointE, startPointI, endPointE, endPointI, direction, _) =>
      val externalRadius = center euclideanDistance startPointE
      val internalRadius = center euclideanDistance startPointI
      drawSingleTurn(externalRadius, center, 2 * externalRadius, direction, g)
      drawSingleTurn(internalRadius, center, 2 * internalRadius, direction, g)
  }

  private def drawSingleTurn(radius: Int, center: Point2d[Int, Int], diameter: Int, direction: Int, g: Graphics): Unit =
    center match {
      case Point2d(x, y) =>
        g.drawArc(x - radius, y - radius, diameter, diameter, TURN_START_ANGLE, TURN_END_ANGLE * direction)
    }

  private def drawCar(position: Point2d[Int, Int], color: Color, g: Graphics): Unit =
    g.setColor(color)
    g.fillOval(position._1, position._2, CAR_DIAMETER, CAR_DIAMETER)
