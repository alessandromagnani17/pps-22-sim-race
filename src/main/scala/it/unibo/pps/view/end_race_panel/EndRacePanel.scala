package it.unibo.pps.view.end_race_panel

import it.unibo.pps.controller.ControllerModule
import it.unibo.pps.model.Car
import it.unibo.pps.utility.{PimpScala, Utility}
import it.unibo.pps.view.main_panel.ImageLoader
import it.unibo.pps.view.end_race_panel.EndRacePanel
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import it.unibo.pps.utility.PimpScala.RichJPanel.*
import it.unibo.pps.utility.GivenConversion.GuiConversion
import it.unibo.pps.view.Constants.StartSimulationPanelConstants.{BUTTONS_HEIGHT, BUTTONS_WIDTH}

import java.awt.{BorderLayout, Color, Dimension, FlowLayout}
import javax.swing.*
import scala.collection.mutable.Map
import it.unibo.pps.view.Constants.EndRacePanelConstants.*
import it.unibo.pps.utility.GivenConversion.GuiConversion.given
import it.unibo.pps.view.Constants.SimulationPanelConstants.AXIS_CHARTS_PANEL

trait EndRacePanel extends JPanel

object EndRacePanel:
  def apply(controller: ControllerModule.Controller): EndRacePanel =
    EndRacePanelImpl(controller)

  private class EndRacePanelImpl(controller: ControllerModule.Controller) extends EndRacePanel:
    self =>

    private val standingsPanel = createStandingsPanel()
    private val mainPanel = createPanelAndAddAllComponents()

    mainPanel foreach (e => self.add(e))

    private def createStandingsPanel(): Task[JPanel] =
      for
        panel <- JPanel()
        _ <- panel.setPreferredSize(Dimension(STANDINGS_PANEL_WIDTH, STANDINGS_PANEL_HEIGHT))
      yield panel

    private def createPanelAndAddAllComponents(): Task[JPanel] =
      for
        panel <- JPanel()
        _ <- panel.setLayout(new BoxLayout(panel, AXIS_CHARTS_PANEL))
        _ <- panel.setPreferredSize(Dimension(FRAME_WIDTH, FRAME_HEIGHT))
        titleLabel <- JLabel("Final Standings:")
        _ <- titleLabel.setPreferredSize(Dimension(FRAME_WIDTH, STANDINGS_TITLE_LABEL_HEIGHT))
        _ <- titleLabel.setVerticalAlignment(SwingConstants.BOTTOM)
        _ <- titleLabel.setHorizontalAlignment(SwingConstants.CENTER)
        standingsPanel <- standingsPanel
        _ <- controller.standings.standings.foreach(car => addToPanel(car, standingsPanel))
        _ <- panel.add(titleLabel)
        _ <- panel.add(standingsPanel)
        restartP <- JPanel()
        restartButton <- JButton("Start a new simulation")
        _ <- restartButton.addActionListener(e => controller.startNewSimulation)
        _ <- restartButton.setPreferredSize(Dimension(BUTTONS_WIDTH, BUTTONS_HEIGHT))
        _ <- restartP.add(restartButton)
        _ <- panel.add(restartP)
        _ <- panel.setVisible(true)
      yield panel

    private def addToPanel(car: Car, panel: JPanel): Unit =
      val p = for
        p <- JPanel()
        _ <- p.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.BLACK))
        position <- JLabel((controller.standings.standings.indexOf(car) + 1).toString)
        name <- JLabel(car.name)
        color <- JLabel()
        img <- JLabel(ImageLoader.load(s"/cars/miniatures/${CAR_NAMES.find(_._2.equals(car.name)).get._1}.png"))
        tyre <- JLabel(car.tyre.toString)
        degradation <- JLabel(s"${(car.degradation * 100).toInt}%")
        fuel <- JLabel(s"${car.fuel.toInt} / ${MAX_FUEL}L")
        time <- JLabel(Utility.calcGapToLeader(car, controller.standings))
        fastestLap <- JLabel(Utility.convertTimeToMinutes(car.fastestLap))
        fastestLapIcon <- JLabel(ImageLoader.load("/fastest-lap-logo.png"))
        paddingLabel <- JLabel()
        paddingLabel1 <- JLabel()
        _ <- paddingLabel.setPreferredSize(Dimension(STANDINGS_PADDING_WIDTH, STANDINGS_COMPONENT_HEIGHT))
        _ <- paddingLabel1.setPreferredSize(Dimension(STANDINGS_PADDING_WIDTH, STANDINGS_COMPONENT_HEIGHT))
        _ <- position.setPreferredSize(Dimension(STANDINGS_POSITION_WIDTH, STANDINGS_COMPONENT_HEIGHT))
        _ <- name.setPreferredSize(Dimension(STANDINGS_NAME_WIDTH, STANDINGS_COMPONENT_HEIGHT))
        _ <- color.setPreferredSize(Dimension(STANDINGS_COLOR_WIDTH, STANDINGS_COLOR_HEIGHT))
        _ <- color.setBackground(car.renderCarParams.color)
        _ <- color.setOpaque(true)
        _ <- tyre.setPreferredSize(Dimension(STANDINGS_TYRE_WIDTH, STANDINGS_COMPONENT_HEIGHT))
        _ <- degradation.setPreferredSize(Dimension(STANDINGS_TYRE_WIDTH, STANDINGS_COMPONENT_HEIGHT))
        _ <- fuel.setPreferredSize(Dimension(STANDINGS_FUEL_WIDTH, STANDINGS_COMPONENT_HEIGHT))
        _ <- time.setPreferredSize(Dimension(STANDINGS_TIME_WIDTH, STANDINGS_COMPONENT_HEIGHT))
        _ <- time.setHorizontalAlignment(SwingConstants.CENTER)
        _ <- fastestLap.setPreferredSize(Dimension(STANDINGS_TIME_WIDTH, STANDINGS_COMPONENT_HEIGHT))
        _ <- fastestLap.setHorizontalAlignment(SwingConstants.CENTER)
        _ <-
          if controller.fastestCar.equals(car.name) then fastestLapIcon.setVisible(true)
          else fastestLapIcon.setVisible(false)
        _ <- p.addAll(
          List(position, name, color, paddingLabel, img, paddingLabel1, tyre, degradation, fuel, time, fastestLap,
            fastestLapIcon)
        )
        _ <- panel.add(p)
      yield ()
      p.runSyncUnsafe()
