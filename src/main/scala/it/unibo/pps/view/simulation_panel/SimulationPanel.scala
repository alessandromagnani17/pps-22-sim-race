package it.unibo.pps.view.simulation_panel

import it.unibo.pps.controller.ControllerModule
import it.unibo.pps.model.car.{Car, CarColors}
import it.unibo.pps.model.track.Track
import java.awt.{
  BorderLayout,
  Color,
  Component,
  Dimension,
  FlowLayout,
  Graphics,
  GridBagConstraints,
  GridBagLayout,
  GridLayout
}
import javax.swing.{
  BorderFactory,
  BoxLayout,
  ImageIcon,
  JButton,
  JComponent,
  JLabel,
  JList,
  JPanel,
  JScrollPane,
  JTable,
  JTextArea,
  SwingConstants,
  SwingUtilities,
  WindowConstants
}
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import it.unibo.pps.view.charts.LineChart
import org.jfree.chart.ChartPanel
import it.unibo.pps.model.{Snapshot, Standings}
import it.unibo.pps.utility.PimpScala.RichTuple2.*
import java.awt.event.{ActionEvent, ActionListener}
import scala.concurrent.duration.FiniteDuration
import it.unibo.pps.view.Constants.SimulationPanelConstants.*
import it.unibo.pps.view.main_panel.ImageLoader
import concurrent.duration.{Duration, DurationInt, FiniteDuration}
import scala.collection.mutable.Map
import scala.language.postfixOps
import scala.language.implicitConversions
import it.unibo.pps.utility.PimpScala.RichJPanel.*
import it.unibo.pps.utility.GivenConversion.GuiConversion.given
import scala.math.BigDecimal

trait SimulationPanel extends JPanel:

  /** Renders the new snapshot of the simulation */
  def render(cars: List[Car], actualLap: Int, totalLaps: Int): Unit

  /** Renders the track, it must be used when showing simulation panel for first time */
  def renderTrack(track: Track): Unit

  /** Method that sets enabled the final report button that if pressed, display the end race panel */
  def setFinalReportEnabled(): Unit

  /** Method that updates the displayed standings */
  def updateDisplayedStandings(): Unit

  /** Updates all the charts
    * @param snapshot
    *   Last snapshot of the simulation
    */
  def updateCharts(snapshot: Snapshot): Unit

  /** Method that updates the fastest lap icon */
  def updateFastestLapIcon(carName: String): Unit

object SimulationPanel:

  def apply(controller: ControllerModule.Controller): SimulationPanel =
    new SimulationPanelImpl(controller)

  private class SimulationPanelImpl(controller: ControllerModule.Controller) extends SimulationPanel:
    self =>

    private lazy val canvas =
      for
        cnv <- new Environment(CANVAS_WIDTH, CANVAS_HEIGHT)
        _ <- cnv.setPreferredSize(Dimension(CANVAS_WIDTH, CANVAS_HEIGHT))
        _ <- cnv.setVisible(true)
      yield cnv

    private val charts = createCharts()

    private lazy val chartsPanel =
      for
        p <- new JPanel()
        _ <- p.setLayout(new BoxLayout(p, AXIS_CHARTS_PANEL))
        chPanels <- Task(charts.map(_.wrapToPanel))
        _ <- chPanels.foreach(_.setPreferredSize(new Dimension(CHART_WIDTH, CHART_HEIGHT)))
        _ <- p.addAll(chPanels)
        sp <- new JScrollPane(p)
        _ <- sp.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_AS_NEEDED)
        _ <- sp.setPreferredSize(new Dimension(CHART_PANEL_WIDTH, CHART_PANEL_HEIGHT))
        _ <- controller.registerReactiveChartCallback
      yield sp

    private lazy val standingsComponents = createStandingsComponent()

    private lazy val standings =
      for
        panel <- JPanel()
        _ <- panel.setPreferredSize(Dimension(CANVAS_WIDTH, STANDINGS_PANEL_HEIGHT))
      yield panel

    private lazy val reportButton = for
      btn <- JButton("Final report")
      _ <- btn.setEnabled(false)
      _ <- btn.addActionListener { e =>
        controller.displayEndRacePanel
      }
    yield btn

    private lazy val p = for
      cnv <- canvas
      scrollPanel <- chartsPanel
      startButton <- createButton(
        "Start",
        e =>
          e.getSource.asInstanceOf[JButton].setEnabled(false)
          controller.notifyStart
      )
      stopButton <- createButton(
        "Stop",
        e =>
          startButton.setEnabled(true)
          controller.notifyStop
      )
      incVelocityButton <- createButton("+ Velocity", e => controller.notifyIncreaseSpeed)
      decVelocityButton <- createButton("- Velocity", e => controller.notifyDecreaseSpeed)
      reportButton <- reportButton
      s <- standings
      buttonsPanel = new JPanel()
      _ <- buttonsPanel.setPreferredSize(Dimension(FRAME_WIDTH, BUTTONS_PANEL_HEIGHT))
      mainPanel = new JPanel()
      _ <- mainPanel.setPreferredSize(Dimension(CANVAS_WIDTH, (FRAME_HEIGHT * 0.9).toInt))
      _ <- buttonsPanel.addAll(List(startButton, stopButton, incVelocityButton, decVelocityButton, reportButton))
      _ <- mainPanel.addAll(List(cnv, s))
      _ <- standingsComponents.foreach(e => addToPanel(e, s))
      _ <- self.addAll(List(mainPanel, scrollPanel, buttonsPanel))
    yield ()
    p.runAsyncAndForget

    override def render(cars: List[Car], actualLap: Int, totalLaps: Int): Unit = SwingUtilities.invokeLater { () =>
      val p = for
        cnv <- canvas
        _ <- cnv.cars = cars
        _ <- cnv.actualLap = actualLap
        _ <- cnv.totalLaps = totalLaps
        _ <- cnv.invalidate()
        _ <- cnv.repaint()
      yield ()
      p.runSyncUnsafe()
    }

    override def setFinalReportEnabled(): Unit =
      val p = for
        reportButton <- reportButton
        _ <- reportButton.setEnabled(true)
      yield ()
      p.runSyncUnsafe()

    override def renderTrack(track: Track): Unit = SwingUtilities.invokeLater { () =>
      val p = for
        cnv <- canvas
        _ <- cnv.track = track
        _ <- cnv.invalidate()
        _ <- cnv.repaint()
      yield ()
      p.runSyncUnsafe()
    }

    private val matchChart = (chart: LineChart, snapshot: Snapshot) =>
      chart.title match
        case s: String if s.equals("Velocity") =>
          snapshot.cars.foreach(car => chart.addValue(snapshot.time, car.actualSpeed, car.name))
        case s: String if s.equals("Fuel") =>
          snapshot.cars.foreach(car => chart.addValue(snapshot.time, car.fuel, car.name))
        case s: String if s.equals("Degradation") =>
          snapshot.cars.foreach(car => chart.addValue(car.actualLap, car.degradation, car.name))
        case _ =>

    override def updateCharts(snapshot: Snapshot): Unit =
      charts.foreach(c => c.foreach(chart => matchChart(chart, snapshot)))

    override def updateDisplayedStandings(): Unit = // TODO PROVARE A CAMBIARE
      standingsComponents.zipWithIndex.foreach((e, i) =>
        val car = controller.standings.standings(i)
        e.name.foreach(f => f.setText(car.name))
        e.color.foreach(f => f.setBackground(car.renderCarParams.color))
        e.miniature.foreach(f =>
          f.setIcon(
            ImageLoader.load(
              s"/cars/miniatures/${CAR_NAMES.find(_._2.equals(car.name)).get._1}.png"
            )
          )
        )
        e.tyres.foreach(f => f.setText(car.tyre.toString))
        e.raceTime.foreach(f => f.setText(controller.calcGapToLeader(car)))
        e.lapTime.foreach(f => f.setText(controller.convertTimeToMinutes(car.lapTime)))
        e.fastestLap.foreach(f => f.setText(controller.convertTimeToMinutes(car.fastestLap)))
      )

    override def updateFastestLapIcon(carName: String): Unit =
      standingsComponents.foreach(e =>
        e.name.foreach(f =>
          if f.getText.equals(carName) then e.fastestLapIcon.foreach(c => c.setVisible(true))
          else e.fastestLapIcon.foreach(c => c.setVisible(false))
        )
      )

    private def createCharts(): List[LineChart] =
      val chartVel = LineChart("Velocity", "Virtual Time", "Velocity (km/h)")
      val chartFuel = LineChart("Fuel", "Virtual Time", "Fuel (l)")
      val chartTyres = LineChart("Degradation", "Lap", "Degradation (%)")
      val c = List(chartVel, chartTyres, chartFuel)
      c.foreach(addSeriesToChart(_))
      c

    private def addSeriesToChart(chart: LineChart): Unit =
      controller.cars.foreach(c => chart.addSeries(c.name, CarColors.getColor(c.name)))

    private def createButton(title: String, listener: ActionListener): Task[JButton] =
      for
        jb <- new JButton()
        _ <- jb.setText(title)
        _ <- jb.addActionListener(listener)
      yield jb

    private def addToPanel(elem: StandingsComponents, mainPanel: JPanel): Task[Unit] =
      val start = 0
      val p = for
        panel <- JPanel(FlowLayout(FlowLayout.LEFT))
        _ <- panel.setPreferredSize(Dimension(CANVAS_WIDTH, STANDINGS_SUBPANEL_HEIGHT))
        _ <- panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.BLACK))
        pos <- elem.position
        name <- elem.name
        color <- elem.color
        miniature <- elem.miniature
        tyre <- elem.tyres
        raceTime <- elem.raceTime
        lapTime <- elem.lapTime
        fastestLap <- elem.fastestLap
        fastestLapIcon <- elem.fastestLapIcon
        paddingLabel <- JLabel()
        paddingLabel1 <- JLabel()
        _ <- paddingLabel.setPreferredSize(Dimension(PADDING_LABEL_WIDTH, STANDINGS_SUBPANEL_HEIGHT))
        _ <- paddingLabel1.setPreferredSize(Dimension(PADDING_LABEL_WIDTH, STANDINGS_SUBPANEL_HEIGHT))
        _ <- color.setBackground(CarColors.getColor(name.getText))
        _ <- color.setOpaque(true)
        _ <- fastestLapIcon.setVisible(false)
        _ <- panel.addAll(
          List(pos, name, color, paddingLabel, miniature, paddingLabel1, tyre, raceTime, lapTime, fastestLap,
            fastestLapIcon)
        )
        _ <- mainPanel.add(panel)
      yield ()
      p.runAsyncAndForget

    private def createStandingsComponent(): List[StandingsComponents] =
      for car <- controller.startingPositions
      yield StandingsComponents(
        createLabel(
          Option(Dimension(STANDINGS_SUBLABEL_WIDTH, STANDINGS_SUBPANEL_HEIGHT)),
          () => Left((controller.startingPositions.indexOf(car) + 1).toString)
        ),
        createLabel(Option(Dimension(STANDINGS_NAME_WIDTH, STANDINGS_SUBPANEL_HEIGHT)), () => Left(car.name)),
        createLabel(Option(Dimension(STANDINGS_COLOR_WIDTH, STANDINGS_SUBPANEL_HEIGHT)), () => Left("")),
        createLabel(
          Option.empty,
          () => Right(ImageLoader.load(s"/cars/miniatures/${controller.startingPositions.indexOf(car)}.png"))
        ),
        createLabel(
          Option(Dimension(STANDINGS_SUBLABEL_WIDTH, STANDINGS_SUBPANEL_HEIGHT)),
          () => Left(car.tyre.toString)
        ),
        createLabel(
          Option(Dimension(STANDINGS_SUBLABEL_WIDTH, STANDINGS_SUBPANEL_HEIGHT)),
          () => Left(car.raceTime.toString)
        ),
        createLabel(
          Option(Dimension(STANDINGS_SUBLABEL_WIDTH, STANDINGS_SUBPANEL_HEIGHT)),
          () => Left(car.lapTime.toString)
        ),
        createLabel(
          Option(Dimension(STANDINGS_SUBLABEL_WIDTH, STANDINGS_SUBPANEL_HEIGHT)),
          () => Left(car.fastestLap.toString)
        ),
        createLabel(Option.empty, () => Right(ImageLoader.load("/fastest-lap-logo.png")))
      )

    private def createLabel(dim: Option[Dimension], f: () => Either[String, ImageIcon]): Task[JLabel] =
      for
        label <- f() match
          case Left(s: String) => JLabel(s)
          case Right(i: ImageIcon) => JLabel(i)
        _ <- if dim.isDefined then
          label.setPreferredSize(dim.get)
          label.setHorizontalAlignment(SwingConstants.CENTER)
      yield label
