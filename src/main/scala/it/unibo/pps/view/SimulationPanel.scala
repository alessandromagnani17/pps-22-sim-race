package it.unibo.pps.view

import it.unibo.pps.controller.ControllerModule

import java.awt.{BorderLayout, Color, Component, Dimension, Graphics}
import javax.swing.{BoxLayout, JButton, JComponent, JLabel, JPanel, JScrollPane, JTextArea, SwingUtilities, WindowConstants}
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import it.unibo.pps.view.charts.LineChart
import org.jfree.chart.ChartPanel
import it.unibo.pps.model.{Sector, Track, TrackBuilder}
import it.unibo.pps.model.Sector.radius

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
        val trackBuilder = TrackBuilder()
        c.track = trackBuilder.createBaseTrack(c.w, c.h)
      )

class Enviroment(val w: Int, val h: Int) extends JPanel:

  var track: Track = Track()

  override def getPreferredSize: Dimension = new Dimension(w, h)
  override def paintComponent(g: Graphics): Unit =
    g.setColor(Color.BLACK)

    def matcher(e: Sector) = e match {
      case s: Sector.Straight => drawStraigth(s, g)
      case t: Sector.Turn => println("ciao")
    }

    track.getSectors().foreach(matcher(_))
  
  private def drawStraigth(s: Sector.Straight, g: Graphics): Unit =
    val p0 = s.drawingParams.p0
    val p1 = s.drawingParams.p1
    val p2 = s.drawingParams.p2
    val p3 = s.drawingParams.p3

    g.drawLine(p0._1, p0._2, p1._1, p1._2)
    g.drawLine(p2._1, p2._2, p3._1, p3._2)

  /*
  private def drawTurn(t: Sector.Turn, g: Graphics): Unit =
    val r = radius(t)
    val x = t.center._1 - r
    val y = t.center._2 - r
    var width: Int = 2 * r
    var height: Int = 2 * r
    var startAngle: Int = 270
    var endAngle: Int = 180*t.direction
    g.drawArc(x, y, width, height, startAngle, endAngle)
 */



