package it.unibo.pps.view

import it.unibo.pps.controller.ControllerModule

import java.awt.{BorderLayout, Color, Component, Dimension, Graphics}
import javax.swing.{
  BoxLayout,
  JButton,
  JComponent,
  JLabel,
  JPanel,
  JScrollPane,
  JTextArea,
  SwingUtilities,
  WindowConstants
}
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import it.unibo.pps.view.charts.LineChart
import org.jfree.chart.ChartPanel
import it.unibo.pps.model.Sector
import it.unibo.pps.model.Sector.radius
import it.unibo.pps.model.Track

import java.awt.event.{ActionEvent, ActionListener}
import scala.math.atan2

trait SimulationPanel extends JPanel:

  /** Method for rendering the new snapshot of the simulation */
  def render(): Unit

object SimulationPanel:

  given Conversion[Enviroment, Task[Enviroment]] = Task(_)
  given Conversion[Int, Task[Int]] = Task(_)
  given Conversion[Unit, Task[Unit]] = Task(_)
  given Conversion[JButton, Task[JButton]] = Task(_)
  given Conversion[Component, Task[Component]] = Task(_)
  given Conversion[JPanel, Task[JPanel]] = Task(_)
  given Conversion[JScrollPane, Task[JScrollPane]] = Task(_)
  given Conversion[LineChart, Task[LineChart]] = Task(_)
  given Conversion[ChartPanel, Task[ChartPanel]] = Task(_)

  def apply(width: Int, height: Int, controller: ControllerModule.Controller): SimulationPanel =
    new SimulationPanelImpl(width, height, controller)

  private class SimulationPanelImpl(width: Int, height: Int, controller: ControllerModule.Controller)
      extends SimulationPanel:
    self =>
    private val cnv = createCanvas()
    val p = for
      _ <- self.setLayout(new BorderLayout())
      canvas <- cnv
      scrollPanel <- createChartsPanel()
      startButton <- createButton("Start", e => println("button start pressed"))
      stopButton <- createButton("Stop", e => println("button stop pressed"))
      incVelocityButton <- createButton("+ Velocity", e => println("button incVel pressed"))
      decVelocityButton <- createButton("- Velocity", e => println("button decVel pressed"))
      buttonsPanel = new JPanel()
      _ <- buttonsPanel.add(startButton)
      _ <- buttonsPanel.add(stopButton)
      _ <- buttonsPanel.add(incVelocityButton)
      _ <- buttonsPanel.add(decVelocityButton)
      _ <- self.add(scrollPanel, BorderLayout.EAST)
      _ <- self.add(buttonsPanel, BorderLayout.SOUTH)
      _ <- self.add(canvas, BorderLayout.WEST)
      _ <- initTrack()
      _ <- render()
    yield ()
    p.runAsyncAndForget

    override def render(): Unit = SwingUtilities.invokeLater { () =>
      cnv.foreach(c =>
        c.invalidate()
        c.repaint()
      )
    }

    private def createCanvas(): Task[Enviroment] =
      val w = (width * 0.6).toInt
      val h = (height * 0.7).toInt
      for
        cnv <- new Enviroment(w, h)
        _ <- cnv.setSize(w, h)
        _ <- cnv.setVisible(true)
      yield cnv

    private def createButton(title: String, listener: ActionListener): Task[JButton] =
      for
        jb <- new JButton()
        _ <- jb.setText(title)
        _ <- jb.addActionListener(listener)
      yield jb

    private def createChartsPanel(): Task[JScrollPane] =
      for
        p <- new JPanel()
        _ <- p.setLayout(new BoxLayout(p, 1))
        w = (width * 0.35).toInt
        h = 300
        chartVel <- createChart("Mean velocity", "Virtual Time", "Velocity", "Velocity")
        chartVelP <- chartVel.getPanel()
        _ <- chartVelP.setPreferredSize(new Dimension(w, h))
        chartFuel <- createChart("Mean fuel", "Virtual Time", "Fuel", "Fuel")
        chartFuelP <- chartFuel.getPanel()
        _ <- chartFuelP.setPreferredSize(new Dimension(w, h))
        chartTyres <- createChart("Tyres degradation", "Virtual Time", "Degradation", "Degradation")
        chartTyresP <- chartTyres.getPanel()
        _ <- chartTyresP.setPreferredSize(new Dimension(w, h))
        _ <- p.add(chartVelP)
        _ <- p.add(chartFuelP)
        _ <- p.add(chartTyresP)
        sp <- new JScrollPane(p)
        _ <- sp.setVerticalScrollBarPolicy(22)
        _ <- sp.setPreferredSize(new Dimension((width * 0.4).toInt, (height * 0.7).toInt))
      yield sp

    private def createChart(title: String, xLabel: String, yLabel: String, serieName: String): Task[LineChart] =
      for chart <- LineChart(title, xLabel, yLabel, serieName)
      yield chart

    private def initTrack(): Unit =
      cnv.foreach(c =>
        val w1 = (0.3 * c.w).toInt
        val w2 = (0.7 * c.w).toInt
        val h1 = (0.3 * c.h).toInt
        val h2 = (0.7 * c.h).toInt
        var x0 = w2 //Da cambiare metto il riferimento a w1
        val y0 = (h1 + h2) / 2 //Da cambiare metto il riferimento a w1
        val x1 = w2
        val x2 = w2
        val y1 = h1
        val y2 = h2
        c.track.addSector(Sector.Straight(1, w1, h1, w2, h1))
        c.track.addSector(Sector.Straight(3, w1, h2, w2, h2))
        c.track.addSector(Sector.Turn(2, (x0, y0), (x1, y1), (x2, y2)))
      )

class Enviroment(val w: Int, val h: Int) extends JPanel:

  var track: Track = Track()

  override def getPreferredSize: Dimension = new Dimension(w, h)
  override def paintComponent(g: Graphics): Unit =
    g.setColor(Color.BLACK)

    def matcher(e: Sector) = e match {
      case s: Sector.Straight => g.drawLine(s.initialX, s.initialY, s.finalX, s.finalY)
      case t: Sector.Turn => drawTurn(t, g)
    }

    track.getSectors().foreach(matcher(_))

  /*var w1 = (0.30 * w).toInt
    var w2 = (0.70 * w).toInt
    var h1 = (0.30 * h).toInt
    var h2 = (0.70 * h).toInt

    g.drawLine(w1, h1, w2, h1)
    g.drawLine(w1, h2, w2, h2)

    var x0 = w2 //Da cambiare metto il riferimento a w1
    var y0 = (h1 + h2) / 2 //Da cambiare metto il riferimento a w1
    g.drawOval(x0, y0, 3, 3)

    var x1 = w2
    var x2 = w2
    var y1 = h1
    var y2 = h2

    drawTurn(Sector.Turn(1, (x0, y0), (x1, y1), (x2, y2)), g)*/

  //track.getSectors().foreach(matcher(_))

  /* var w1 = (0.30 * w).toInt
    var w2 = (0.70 * w).toInt
    var h1 = (0.30 * h).toInt
    var h2 = (0.70 * h).toInt

    g.drawLine(w1, h1, w2, h1)
    g.drawLine(w1, h2, w2, h2)

    var x0 = w2 //Da cambiare metto il riferimento a w1
    var y0 = (h1 + h2) / 2 //Da cambiare metto il riferimento a w1
    g.drawOval(x0, y0, 3, 3)

    var x1 = w2
    var x2 = w2
    var y1 = h1
    var y2 = h2

    //Arco grande a Dx
    var r: Int = Math.sqrt((x1 - x0) * (x1 - x0) + (y1 - y0) * (y1 - y0)).toInt
    var x: Int = x0 - r
    var y: Int = y0 - r
    var width: Int = 2 * r
    var height: Int = 2 * r
    var startAngle: Int = (180 / Math.PI * atan2(y1 - y0, x1 - x0)).asInstanceOf[Int]
    var endAngle: Int = (360 / Math.PI * atan2(y2 - y0, x2 - x0)).asInstanceOf[Int]
    //g.drawRect(x, y, width, height)
    g.drawArc(x, y, width, height, startAngle, endAngle) */

  /*var w1 = (0.30 * w).toInt
    var w2 = (0.70 * w).toInt
    var h1 = (0.30 * h).toInt
    var h2 = (0.70 * h).toInt

    g.drawLine(w1, h1, w2, h1)
    g.drawLine(w1, h2, w2, h2)

    var x0 = w2 //Da cambiare metto il riferimento a w1
    var y0 = (h1 + h2) / 2 //Da cambiare metto il riferimento a w1
    var x1 = w2
    var x2 = w2
    var y1 = h1
    var y2 = h2

    //Arco grande a Dx
    var r: Int = Math.sqrt((x1 - x0) * (x1 - x0) + (y1 - y0) * (y1 - y0)).toInt
    var x: Int = x0 - r
    var y: Int = y0 - r
    var width: Int = 2 * r
    var height: Int = 2 * r
    var startAngle: Int = (180 / Math.PI * atan2(y1 - y0, x1 - x0)).asInstanceOf[Int]
    var endAngle: Int = (360 / Math.PI * atan2(y2 - y0, x2 - x0)).asInstanceOf[Int]
    g.drawArc(x, y, width, height, startAngle, endAngle)

    //Arco grande a Sx
    x0 = w1 //Da cambiare metto il riferimento a w1
    y0 = (h1 + h2) / 2 //Da cambiare metto il riferimento a w1
    x1 = w1
    x2 = w1
    y1 = h1
    y2 = h2
    r = Math.sqrt((x1 - x0) * (x1 - x0) + (y1 - y0) * (y1 - y0)).toInt
    x = x0 - r
    y = y0 - r
    width = 2 * r
    height = 2 * r
    startAngle = (-180 / Math.PI * atan2(y1 - y0, x1 - x0)).asInstanceOf[Int]
    endAngle = (360 / Math.PI * atan2(y2 - y0, x2 - x0)).asInstanceOf[Int]
    g.drawArc(x, y, width, height, startAngle, endAngle)

    //Combio delle coordinate principali
    h1 = (0.40 * h).toInt
    h2 = (0.60 * h).toInt
    g.drawLine(w1, h1, w2, h1)
    g.drawLine(w1, h2, w2, h2)
    x0 = w2 //Da cambiare metto il riferimento a w1
    y0 = (h1 + h2) / 2 //Da cambiare metto il riferimento a w1
    x1 = w2
    x2 = w2
    y1 = h1
    y2 = h2

    //Arco piccolo a Dx
    r = Math.sqrt((x1 - x0) * (x1 - x0) + (y1 - y0) * (y1 - y0)).toInt
    x = x0 - r
    y = y0 - r
    width = 2 * r
    height = 2 * r
    startAngle = (180 / Math.PI * atan2(y1 - y0, x1 - x0)).asInstanceOf[Int]
    endAngle = (360 / Math.PI * atan2(y2 - y0, x2 - x0)).asInstanceOf[Int]
    g.drawArc(x, y, width, height, startAngle, endAngle)

    //Arco piccola a Sx
    x0 = w1 //Da cambiare metto il riferimento a w1
    y0 = (h1 + h2) / 2 //Da cambiare metto il riferimento a w1
    x1 = w1
    x2 = w1
    y1 = h1
    y2 = h2
    r = Math.sqrt((x1 - x0) * (x1 - x0) + (y1 - y0) * (y1 - y0)).toInt
    x = x0 - r
    y = y0 - r
    width = 2 * r
    height = 2 * r
    startAngle = (-180 / Math.PI * atan2(y1 - y0, x1 - x0)).asInstanceOf[Int]
    endAngle = (360 / Math.PI * atan2(y2 - y0, x2 - x0)).asInstanceOf[Int]
    g.drawArc(x, y, width, height, startAngle, endAngle)

    //g.setColor(Color.BLACK)
    //g.fillRect(0, 0, w, h)

    println("centro: " + w1 + ", " + (h1 + h2) / 2)
    println("prima linea: " + w1 + ", " + h1)
    println("seconda linea: " + w1 + ", " + h2)*/

  private def drawTurn(t: Sector.Turn, g: Graphics): Unit =
    val r = radius(t)
    val x = t.center._1 - r
    val y = t.center._2 - r
    var width: Int = 2 * r
    var height: Int = 2 * r
    var startAngle: Int = 270
    var endAngle: Int = 180
    g.drawArc(x, y, width, height, startAngle, endAngle)
