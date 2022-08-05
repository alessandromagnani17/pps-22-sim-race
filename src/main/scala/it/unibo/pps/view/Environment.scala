package it.unibo.pps.view

import it.unibo.pps.model.{Sector, Track}

import java.awt.{Color, Dimension, Graphics}
import javax.swing.JPanel
import it.unibo.pps.util.PimpScala.RichTuple2._

class Enviroment(val w: Int, val h: Int) extends JPanel:

  var track: Track = Track()

  override def getPreferredSize: Dimension = new Dimension(w, h)
  override def paintComponent(g: Graphics): Unit =
    g.setColor(Color.BLACK)

//---------------------------------------------->>>> MACCHINE <<<<-------------------------------------------------------

    //Variabili relative alle macchine
    var intW = (w * 0.5).toInt
    var intH = (h * 0.3).toInt
    var carDiameter = 20
    var distanceBeetweenCars = 20

    //Metodo 1 per disegnare le macchine

    g.setColor(Color.BLUE)
    g.fillOval(intW, intH, carDiameter, carDiameter)
    g.setColor(Color.GREEN)
    g.fillOval(intW + distanceBeetweenCars, intH + distanceBeetweenCars, carDiameter, carDiameter)
    g.setColor(Color.RED)
    g.fillOval(intW + (distanceBeetweenCars*2), intH + (distanceBeetweenCars*2), carDiameter, carDiameter)
    g.setColor(Color.BLACK)


    //Metodo 2 per disegnare le macchine
    /*
    Un'idea potrebbe essere quella di memorizzare una shape dentro alla classe macchina, oltre a gli altri parametri
    */

    /*
    import java.awt.Graphics2D
    import java.awt.Shape
    import java.awt.geom.Ellipse2D

    val g2d: Graphics2D = g.asInstanceOf[Graphics2D]

    val car1: Shape = new Ellipse2D.Double(intW, intH, carDiameter, carDiameter)
    val car2: Shape = new Ellipse2D.Double(intW + distanceBeetweenCars, intH + distanceBeetweenCars, carDiameter, carDiameter)
    val car3: Shape = new Ellipse2D.Double(intW + (distanceBeetweenCars*2), intH + (distanceBeetweenCars*2), carDiameter, carDiameter)

    g2d.setColor(Color.CYAN)
    g2d.fill(car1)
    g2d.setColor(Color.MAGENTA)
    g2d.fill(car2)
    g2d.setColor(Color.ORANGE)
    g2d.fill(car3)
    g2d.setColor(Color.BLACK)
    */
//----------------------------------------------------------------------------------------------------------------------

    def sketcher(e: Sector) = e match {
      case s: Sector.Straight => drawStraigth(s, g)
      case t: Sector.Turn => drawTurn(t, g)
    }

    track.getSectors().foreach(sketcher(_))

  private def drawStraigth(s: Sector.Straight, g: Graphics): Unit =
    val p0 = s.drawingParams.p0
    val p1 = s.drawingParams.p1
    val p2 = s.drawingParams.p2
    val p3 = s.drawingParams.p3

    g.drawLine(p0._1, p0._2, p1._1, p1._2)
    g.drawLine(p2._1, p2._2, p3._1, p3._2)

  private def drawTurn(t: Sector.Turn, g: Graphics): Unit =
    val externalRadius = t.drawingParams.center euclideanDistance t.drawingParams.startPointE
    val interalRadius = t.drawingParams.center euclideanDistance t.drawingParams.startPointI
    drawSingleTurn(
      externalRadius,
      t.drawingParams.center._1,
      t.drawingParams.center._2,
      2 * externalRadius,
      t.drawingParams.direction,
      g
    )
    drawSingleTurn(
      interalRadius,
      t.drawingParams.center._1,
      t.drawingParams.center._2,
      2 * interalRadius,
      t.drawingParams.direction,
      g
    )

  private def drawSingleTurn(radius: Int, x: Int, y: Int, diameter: Int, direction: Int, g: Graphics): Unit =
    val x0 = x - radius
    val y0 = y - radius
    val startAngle = 270
    val endAngle = 180 * direction
    g.drawArc(x0, y0, diameter, diameter, startAngle, endAngle)
