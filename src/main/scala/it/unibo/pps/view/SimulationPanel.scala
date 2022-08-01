package it.unibo.pps.view

import it.unibo.pps.controller.ControllerModule
import java.awt.{BorderLayout, Color, Component, Dimension, Graphics}
import javax.swing.{JButton, JComponent, JLabel, JPanel, JScrollPane, JTextArea, WindowConstants}
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import it.unibo.pps.view.charts.LineChart
import org.jfree.chart.ChartPanel

import java.awt.event.{ActionEvent, ActionListener}

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
        chart <- createChart()
        chartP <- chart.getPanel()
        _ <- chartP.setPreferredSize(new Dimension((width * 0.35).toInt, 300))
        _ <- p.add(chartP)
        sp <- new JScrollPane(p)
        _ <- sp.setVerticalScrollBarPolicy(22)
        _ <- sp.setPreferredSize(new Dimension((width * 0.4).toInt, (height * 0.7).toInt))
      yield sp

    private def createChart(): Task[LineChart] =
      for chart <- LineChart("Prova", "Virtual Time", "Velocity", "vel")
      yield chart

class Enviroment(val w: Int, val h: Int) extends JPanel:
  override def getPreferredSize: Dimension = new Dimension(w, h)
  override def paintComponent(g: Graphics): Unit =
    g.setColor(Color.BLUE)
    g.fillRect(0, 0, w, h)
