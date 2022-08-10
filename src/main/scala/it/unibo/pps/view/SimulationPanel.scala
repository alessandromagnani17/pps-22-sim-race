package it.unibo.pps.view

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
import it.unibo.pps.model.{Sector, Track, TrackBuilder}
import it.unibo.pps.utility.PimpScala.RichTuple2.*

import java.awt.event.{ActionEvent, ActionListener}
import scala.concurrent.duration.FiniteDuration
import it.unibo.pps.view.ViewConstants.*
import concurrent.duration.{Duration, DurationInt, FiniteDuration}
import scala.language.postfixOps
import scala.language.implicitConversions

trait SimulationPanel extends JPanel:

  /** Method for rendering the new snapshot of the simulation */
  def render(): Unit
  def updateStanding(newStanding: String): Unit

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
        chartVel <- createChart("Mean velocity", "Virtual Time", "Velocity", "Velocity")
        chartFuel <- createChart("Mean fuel", "Virtual Time", "Fuel", "Fuel")
        chartTyres <- createChart("Tyres degradation", "Virtual Time", "Degradation", "Degradation")
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

    private var standing = createStanding()

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
      _ <- initTrack(cnv)
      _ <- render()
    yield ()
    p.runAsyncAndForget

    override def render(): Unit = SwingUtilities.invokeLater { () =>
      val p = for
        cnv <- canvas
        _ <- cnv.invalidate()
        _ <- cnv.repaint()
      yield ()
      p.runSyncUnsafe()
    }

    override def updateStanding(newStanding: String): Unit = SwingUtilities.invokeLater { () =>
      val p = for
        s <- standing
        _ <- s.setText(newStanding)
      yield ()
      p.runSyncUnsafe()
    }

    private def createButton(title: String, listener: ActionListener): Task[JButton] =
      for
        jb <- new JButton()
        _ <- jb.setText(title)
        _ <- jb.addActionListener(listener)
      yield jb

    private def createChart(title: String, xLabel: String, yLabel: String, serieName: String): Task[LineChart] =
      for chart <- LineChart(title, xLabel, yLabel, serieName)
      yield chart

    //TODO - Da togliere
    private def initTrack(c: Enviroment): Unit =
      val trackBuilder = TrackBuilder()
      c.track = trackBuilder.createBaseTrack()

    //TODO - Da togliere
    private def createStanding(): Task[JLabel] =
      new JLabel("1) Ferrari - 2) Mercedes - 3) RedBull - 4) McLaren")
