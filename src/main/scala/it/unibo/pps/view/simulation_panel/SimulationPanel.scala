package it.unibo.pps.view.simulation_panel

import it.unibo.pps.controller.ControllerModule
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
import it.unibo.pps.model.{Car, Sector, Snapshot, Standing, Track, TrackBuilder, CarColors}
import it.unibo.pps.utility.PimpScala.RichTuple2.*
import java.awt.event.{ActionEvent, ActionListener}
import scala.concurrent.duration.FiniteDuration
import it.unibo.pps.view.ViewConstants.*
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
  def setFinalReportEnabled(): Unit
  def updateDisplayedStanding(): Unit
  def updateCharts(snapshot: Snapshot): Unit
  def updateFastestLapIcon(carName: String): Unit

object SimulationPanel:

  def apply(width: Int, height: Int, controller: ControllerModule.Controller): SimulationPanel =
    new SimulationPanelImpl(width, height, controller)

  private class SimulationPanelImpl(width: Int, height: Int, controller: ControllerModule.Controller)
      extends SimulationPanel:
    self =>

    private val carNames: Map[Int, String] = Map(0 -> "Ferrari", 1 -> "Mercedes", 2 -> "Red Bull", 3 -> "McLaren")

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
        _ <- p.setLayout(new BoxLayout(p, 1))
        chPanels <- Task(charts.map(_.wrapToPanel))
        _ <- chPanels.foreach(_.setPreferredSize(new Dimension(CHART_WIDTH, CHART_HEIGHT)))
        _ <- p.addAll(chPanels)
        sp <- new JScrollPane(p)
        _ <- sp.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_AS_NEEDED)
        _ <- sp.setPreferredSize(new Dimension(CHART_PANEL_WIDTH, CHART_PANEL_HEIGHT))
        _ <- controller.registerReactiveChartCallback()
      yield sp

    private lazy val standingMap = createPositions()

    private lazy val standing =
      for
        panel <- JPanel()
        _ <- panel.setPreferredSize(Dimension(CANVAS_WIDTH, STANDING_PANEL_HEIGHT))
      yield panel

    private lazy val reportButton = for
      btn <- JButton("Final report")
      _ <- btn.setEnabled(false)
      _ <- btn.addActionListener { e =>
        controller.displayEndRacePanel()
      }
    yield btn

    private lazy val p = for
      cnv <- canvas
      scrollPanel <- chartsPanel
      startButton <- createButton(
        "Start",
        e =>
          e.getSource.asInstanceOf[JButton].setEnabled(false)
          controller.notifyStart()
      )
      stopButton <- createButton(
        "Stop",
        e =>
          startButton.setEnabled(true)
          controller.notifyStop()
      )
      incVelocityButton <- createButton("+ Velocity", e => controller.notifyIncreaseSpeed())
      decVelocityButton <- createButton("- Velocity", e => controller.notifyDecreaseSpeed())
      reportButton <- reportButton
      s <- standing
      buttonsPanel = new JPanel()
      _ <- buttonsPanel.setPreferredSize(Dimension(width, BUTTONS_PANEL_HEIGHT))
      mainPanel = new JPanel()
      _ <- mainPanel.setPreferredSize(Dimension(CANVAS_WIDTH, (FRAME_HEIGHT * 0.9).toInt))
      _ <- buttonsPanel.add(startButton)
      _ <- buttonsPanel.add(stopButton)
      _ <- buttonsPanel.add(incVelocityButton)
      _ <- buttonsPanel.add(decVelocityButton)
      _ <- buttonsPanel.add(reportButton)
      _ <- mainPanel.add(cnv)
      _ <- mainPanel.add(s)
      _ <- standingMap.foreach(e => addToPanel(e, s))
      _ <- self.add(mainPanel)
      _ <- self.add(scrollPanel)
      _ <- self.add(buttonsPanel)
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

    override def updateDisplayedStanding(): Unit =
      standingMap.foreach(e =>
        e._2._2.foreach(f => f.setText(controller.standings._standing(e._1).name))
        e._2._3.foreach(f => f.setBackground(controller.standings._standing(e._1).renderCarParams.color))
        e._2._4.foreach(f =>
          f.setIcon(
            ImageLoader.load(
              s"/cars/miniatures/${carNames.find(_._2.equals(controller.standings._standing(e._1).name)).get._1}.png"
            )
          )
        )
        e._2._5.foreach(f => f.setText(controller.standings._standing(e._1).tyre.toString))
        e._2._6.foreach(f => f.setText(controller.calcCarPosting(controller.standings._standing(e._1))))
        e._2._7.foreach(f => f.setText(controller.convertTimeToMinutes(controller.standings._standing(e._1).lapTime)))
        e._2._8.foreach(f =>
          f.setText(controller.convertTimeToMinutes(controller.standings._standing(e._1).fastestLap))
        )
      )

    override def updateFastestLapIcon(carName: String): Unit =
      standingMap.foreach(e =>
        e._2._2.foreach(f =>
          if f.getText.equals(carName) then e._2._9.foreach(c => c.setVisible(true))
          else e._2._9.foreach(c => c.setVisible(false))
        )
      )

    private def createCharts(): List[LineChart] =
      val chartVel = LineChart("Velocity", "Virtual Time", "Velocity (km/h)")
      val chartFuel = LineChart("Fuel", "Virtual Time", "Fuel (l)")
      val chartTyres = LineChart("Degradation", "Lap", "Degradation (%)")
      val c = List(chartVel, chartFuel, chartTyres)
      c.foreach(addSeriesToChart(_))
      c

    private def addSeriesToChart(chart: LineChart): Unit =
      chart.addSeries("Ferrari", Color.RED)
      chart.addSeries("Mercedes", Color.CYAN)
      chart.addSeries("Red Bull", Color.BLUE)
      chart.addSeries("McLaren", Color.GREEN)

    private def createButton(title: String, listener: ActionListener): Task[JButton] =
      for
        jb <- new JButton()
        _ <- jb.setText(title)
        _ <- jb.addActionListener(listener)
      yield jb

    private def addToPanel(
        elem: (
            Int,
            (
                Task[JLabel],
                Task[JLabel],
                Task[JLabel],
                Task[JLabel],
                Task[JLabel],
                Task[JLabel],
                Task[JLabel],
                Task[JLabel],
                Task[JLabel]
            )
        ),
        mainPanel: JPanel
    ): Task[Unit] =
      val start = 0
      val p = for
        panel <- JPanel(FlowLayout(FlowLayout.LEFT))
        _ <- panel.setPreferredSize(Dimension(CANVAS_WIDTH, STANDING_SUBPANEL_HEIGHT))
        _ <- panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.BLACK))

        pos <- elem._2._1 // Posizione
        name <- elem._2._2 // Nome
        color <- elem._2._3 // Colore
        img <- elem._2._4 // Immagine
        tyre <- elem._2._5 // Gomma
        raceTime <- elem._2._6 // Race time
        lapTime <- elem._2._7 // Lap Time
        fastestTime <- elem._2._8 // Fastest time
        fastestLapIcon <- elem._2._9 // Fastest time icon

        paddingLabel <- JLabel()
        paddingLabel1 <- JLabel()
        _ <- paddingLabel.setPreferredSize(Dimension(PADDING_LABEL_WIDTH, STANDING_SUBPANEL_HEIGHT))
        _ <- paddingLabel1.setPreferredSize(Dimension(PADDING_LABEL_WIDTH, STANDING_SUBPANEL_HEIGHT))
        _ <- color.setBackground(controller.startingPositions(elem._1).renderCarParams.color)
        _ <- color.setOpaque(true)
        _ <- fastestLapIcon.setVisible(false)

        _ <- panel.add(pos)
        _ <- panel.add(name)
        _ <- panel.add(paddingLabel)
        _ <- panel.add(color)
        _ <- panel.add(paddingLabel1)
        _ <- panel.add(img)
        _ <- panel.add(paddingLabel)
        _ <- panel.add(tyre)
        _ <- panel.add(raceTime)
        _ <- panel.add(lapTime)
        _ <- panel.add(fastestTime)
        _ <- panel.add(fastestLapIcon)
        _ <- mainPanel.add(panel)
      yield ()
      p.runAsyncAndForget

    // Posizione - Nome - Colore - Immagine - Gomma
    private def createPositions(): Map[
      Int,
      (
          Task[JLabel],
          Task[JLabel],
          Task[JLabel],
          Task[JLabel],
          Task[JLabel],
          Task[JLabel],
          Task[JLabel],
          Task[JLabel],
          Task[JLabel]
      )
    ] =
      val map: Map[
        Int,
        (
            Task[JLabel],
            Task[JLabel],
            Task[JLabel],
            Task[JLabel],
            Task[JLabel],
            Task[JLabel],
            Task[JLabel],
            Task[JLabel],
            Task[JLabel]
        )
      ] = Map.empty
      controller.startingPositions.foreach(e => {
        map += (e._1 -> (createLabel(
          (e._1 + 1).toString,
          Dimension((CANVAS_WIDTH * 0.1).toInt, STANDING_SUBPANEL_HEIGHT),
          false
        ),
        createLabel(e._2.name, Dimension((CANVAS_WIDTH * 0.15).toInt, STANDING_SUBPANEL_HEIGHT), false),
        createLabel("", Dimension((CANVAS_WIDTH * 0.03).toInt, STANDING_SUBPANEL_HEIGHT), false),
        createLabel(s"/cars/miniatures/${e._1}.png", null, true),
        createLabel(e._2.tyre.toString, Dimension((CANVAS_WIDTH * 0.1).toInt, STANDING_SUBPANEL_HEIGHT), false),
        createLabel(e._2.raceTime.toString, Dimension((CANVAS_WIDTH * 0.1).toInt, STANDING_SUBPANEL_HEIGHT), false),
        createLabel(e._2.lapTime.toString, Dimension((CANVAS_WIDTH * 0.1).toInt, STANDING_SUBPANEL_HEIGHT), false),
        createLabel(e._2.fastestLap.toString, Dimension((CANVAS_WIDTH * 0.1).toInt, STANDING_SUBPANEL_HEIGHT), false),
        createLabel("/fastest-lap-logo.png", null, true)))
      })
      map

    private def createLabel(text: String, dimension: Dimension, isImage: Boolean): Task[JLabel] =
      for
        label <- if isImage then JLabel(ImageLoader.load(text)) else JLabel(text)
        _ <- label.setPreferredSize(dimension)
        _ <- if !isImage then label.setHorizontalAlignment(SwingConstants.CENTER)
      yield label
