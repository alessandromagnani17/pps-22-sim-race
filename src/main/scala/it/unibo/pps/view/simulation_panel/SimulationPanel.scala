package it.unibo.pps.view.simulation_panel

import it.unibo.pps.controller.ControllerModule

import java.awt.{BorderLayout, Color, Component, Dimension, Graphics}
import javax.swing.{
  BoxLayout,
  JButton,
  JComponent,
  JLabel,
  JList,
  JPanel,
  JScrollPane,
  JTable,
  JTextArea,
  SwingUtilities,
  WindowConstants
}
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import it.unibo.pps.view.charts.LineChart
import org.jfree.chart.ChartPanel
import it.unibo.pps.model.{Car, Sector, Standing, Track, TrackBuilder}
import it.unibo.pps.utility.PimpScala.RichTuple2.*

import java.awt.event.{ActionEvent, ActionListener}
import scala.concurrent.duration.FiniteDuration
import it.unibo.pps.view.ViewConstants.*

import concurrent.duration.{Duration, DurationInt, FiniteDuration}
import scala.language.postfixOps
import scala.language.implicitConversions

trait SimulationPanel extends JPanel:

  /** Method for rendering the new snapshot of the simulation */
  def render(cars: List[Car]): Unit
  def renderTrack(track: Track): Unit
  def updateStanding(newStanding: Standing): Unit

object SimulationPanel:

  import it.unibo.pps.utility.GivenConversion.GuiConversion.given

  def apply(width: Int, height: Int, controller: ControllerModule.Controller): SimulationPanel =
    new SimulationPanelImpl(width, height, controller)

  private class SimulationPanelImpl(width: Int, height: Int, controller: ControllerModule.Controller)
      extends SimulationPanel:
    self =>

    private lazy val canvas =
      for
        cnv <- new Enviroment(CANVAS_WIDTH, CANVAS_HEIGHT)
        _ <- cnv.setSize(CANVAS_WIDTH, CANVAS_HEIGHT)
        _ <- cnv.setVisible(true)
      yield cnv

    private lazy val chartsPanel =
      for
        p <- new JPanel()
        _ <- p.setLayout(new BoxLayout(p, 1))
        chartVel <- createChart("Velocity", "Virtual Time", "Velocity")
        chartFuel <- createChart("Fuel", "Virtual Time", "Fuel")
        chartTyres <- createChart("Degradation", "Virtual Time", "Degradation")
        _ <- chartFuel.addSeries("Ferrari")
        _ <- chartFuel.addSeries("Mercedes")
        _ <- chartFuel.addValue(1, 2, "Ferrari")
        _ <- chartFuel.addValue(3, 5, "Ferrari")
        _ <- chartFuel.addValue(6, 4, "Ferrari")
        _ <- chartFuel.addValue(2, 4, "Mercedes")
        _ <- chartFuel.addValue(5, 8, "Mercedes")
        _ <- chartFuel.addValue(6, 6, "Mercedes")
        chartVelP <- chartVel.wrapToPanel()
        chartFuelP <- chartFuel.wrapToPanel()
        chartTyresP <- chartTyres.wrapToPanel()
        _ <- chartVelP.setPreferredSize(new Dimension(CHART_WIDTH, CHART_HEIGHT))
        _ <- chartFuelP.setPreferredSize(new Dimension(CHART_WIDTH, CHART_HEIGHT))
        _ <- chartTyresP.setPreferredSize(new Dimension(CHART_WIDTH, CHART_HEIGHT))
        _ <- p.add(chartVelP)
        _ <- p.add(chartFuelP)
        _ <- p.add(chartTyresP)
        sp <- new JScrollPane(p)
        _ <- sp.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_AS_NEEDED)
        _ <- sp.setPreferredSize(new Dimension(CHART_PANEL_WIDTH, CHART_PANEL_HEIGHT))
      yield sp

    private lazy val standing =
      for label <- new JLabel()
      yield label

    private val p = for
      _ <- self.setLayout(new BorderLayout())
      cnv <- canvas
      scrollPanel <- chartsPanel
      startButton <- createButton("Start", e => controller.notifyStart())
      stopButton <- createButton("Stop", e => controller.notifyStop())
      incVelocityButton <- createButton("+ Velocity", e => controller.notifyIncreaseSpeed())
      decVelocityButton <- createButton("- Velocity", e => controller.notifyDecreseSpeed())
      s <- standing
      buttonsPanel = new JPanel()
      mainPanel = new JPanel(new BorderLayout())
      _ <- buttonsPanel.add(startButton)
      _ <- buttonsPanel.add(stopButton)
      _ <- buttonsPanel.add(incVelocityButton)
      _ <- buttonsPanel.add(decVelocityButton)
      _ <- self.add(scrollPanel, BorderLayout.EAST)
      _ <- self.add(buttonsPanel, BorderLayout.SOUTH)
      _ <- mainPanel.add(cnv, BorderLayout.NORTH)
      _ <- mainPanel.add(s, BorderLayout.CENTER)
      _ <- self.add(mainPanel, BorderLayout.WEST)
    yield ()
    p.runAsyncAndForget

    override def render(cars: List[Car]): Unit = SwingUtilities.invokeLater { () =>
      val p = for
        cnv <- canvas
        _ <- cnv.cars = cars
        _ <- cnv.invalidate()
        _ <- cnv.repaint()
      yield ()
      p.runSyncUnsafe()
    }

    override def renderTrack(track: Track): Unit = SwingUtilities.invokeLater { () =>
      val p = for
        cnv <- canvas
        _ <- cnv.track = track
        _ <- cnv.invalidate()
        _ <- cnv.repaint()
      yield ()
      p.runSyncUnsafe()
    }

    override def updateStanding(newStanding: Standing): Unit = SwingUtilities.invokeLater { () =>
      val p = for
        s <- standing
        _ <- s.setText(getPrintableStanding(newStanding))
      yield ()
      p.runSyncUnsafe()
    }

    private def getPrintableStanding(newStanding: Standing): String =
      newStanding._standing
        .map(_.name)
        .zipWithIndex
        .map((car, index) => (car, index + 1))
        .foldLeft("")((prev: String, t: Tuple2[String, Int]) => prev + s"${t._2}) ${t._1}    ")

    private def createButton(title: String, listener: ActionListener): Task[JButton] =
      for
        jb <- new JButton()
        _ <- jb.setText(title)
        _ <- jb.addActionListener(listener)
      yield jb

    private def createChart(title: String, xLabel: String, yLabel: String): Task[LineChart] =
      for chart <- LineChart(title, xLabel, yLabel)
      yield chart
