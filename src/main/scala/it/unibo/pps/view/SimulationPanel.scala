package it.unibo.pps.view

import it.unibo.pps.controller.ControllerModule

import java.awt.{BorderLayout, Color, Component, Dimension, Graphics}
import javax.swing.{BoxLayout, JButton, JComponent, JLabel, JPanel, JScrollPane, JTextArea, WindowConstants}
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import it.unibo.pps.view.charts.LineChart
import org.jfree.chart.ChartPanel

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
    val p = for
      _ <- self.setLayout(new BorderLayout())
      canvas <- createCanvas()
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
    yield ()
    p.runAsyncAndForget

    override def render(): Unit = ???

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

    //private def createCircuitBorder(w1: Int, w2: Int, h1: Int, h2: Int): Unit =


class Enviroment(val w: Int, val h: Int) extends JPanel:
  override def getPreferredSize: Dimension = new Dimension(w, h)
  override def paintComponent(g: Graphics): Unit =
      var w1 = (0.30 * w).toInt
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
      println("seconda linea: " + w1 + ", " + h2)




