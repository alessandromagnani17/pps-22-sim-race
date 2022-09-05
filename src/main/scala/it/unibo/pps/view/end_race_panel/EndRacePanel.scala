package it.unibo.pps.view.end_race_panel

import it.unibo.pps.controller.ControllerModule
import it.unibo.pps.model.Car
import it.unibo.pps.view.main_panel.ImageLoader
import it.unibo.pps.view.end_race_panel.EndRacePanel
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global

import java.awt.{BorderLayout, Color, Dimension, FlowLayout}
import javax.swing.*
import scala.collection.mutable.Map
import it.unibo.pps.view.Constants.EndRacePanelConstants.*

trait EndRacePanel extends JPanel

object EndRacePanel:
  def apply(controller: ControllerModule.Controller): EndRacePanel =
    EndRacePanelImpl(controller)

  private class EndRacePanelImpl(controller: ControllerModule.Controller) extends EndRacePanel:
    self =>

    import it.unibo.pps.utility.GivenConversion.GuiConversion.given

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
        _ <- panel.setPreferredSize(Dimension(FRAME_WIDTH, FRAME_HEIGHT))
        titleLabel <- JLabel("Final Standings:")
        _ <- titleLabel.setPreferredSize(Dimension(FRAME_WIDTH, STANDINGS_TITLE_LABEL_HEIGHT))
        _ <- titleLabel.setVerticalAlignment(SwingConstants.BOTTOM)
        _ <- titleLabel.setHorizontalAlignment(SwingConstants.CENTER)
        standingsPanel <- standingsPanel
        _ <- controller.standings._standing.foreach(e => addToPanel(e, standingsPanel))
        _ <- panel.add(titleLabel)
        _ <- panel.add(standingsPanel)
        _ <- panel.setVisible(true)
      yield panel

    // Posizione - Nome - Colore - Immagine - Gomme - Tempo
    private def addToPanel(elem: (Int, Car), panel: JPanel): Unit =
      val p = for
        p <- JPanel()
        _ <- p.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.BLACK))
        position <- JLabel((elem._1 + 1).toString)
        name <- JLabel(elem._2.name)
        color <- JLabel()
        img <- JLabel(ImageLoader.load(s"/cars/miniatures/${CAR_NAMES.find(_._2.equals(elem._2.name)).get._1}.png"))
        tyre <- JLabel(elem._2.tyre.toString)
        degradation <- JLabel(s"${(elem._2.degradation * 100).toInt}%")
        fuel <- JLabel(s"${elem._2.fuel.toInt} / ${MAX_FUEL}L")
        time <- JLabel(controller.calcCarPosting(elem._2))
        fastestLap <- JLabel(controller.convertTimeToMinutes(elem._2.fastestLap))
        fastestLapIcon <- JLabel(ImageLoader.load("/fastest-lap-logo.png"))
        paddingLabel <- JLabel()
        paddingLabel1 <- JLabel()
        _ <- paddingLabel.setPreferredSize(Dimension(STANDINGS_PADDING_WIDTH, STANDINGS_COMPONENT_HEIGHT))
        _ <- paddingLabel1.setPreferredSize(Dimension(STANDINGS_PADDING_WIDTH, STANDINGS_COMPONENT_HEIGHT))
        _ <- position.setPreferredSize(Dimension(STANDINGS_POSITION_WIDTH, STANDINGS_COMPONENT_HEIGHT))
        _ <- name.setPreferredSize(Dimension(STANDINGS_NAME_WIDTH, STANDINGS_COMPONENT_HEIGHT))
        _ <- color.setPreferredSize(Dimension(STANDINGS_COLOR_WIDTH, STANDINGS_COLOR_HEIGHT))
        _ <- color.setBackground(elem._2.renderCarParams.color)
        _ <- color.setOpaque(true)
        _ <- tyre.setPreferredSize(Dimension(STANDINGS_TYRE_WIDTH, STANDINGS_COMPONENT_HEIGHT))
        _ <- degradation.setPreferredSize(Dimension(STANDINGS_TYRE_WIDTH, STANDINGS_COMPONENT_HEIGHT))
        _ <- fuel.setPreferredSize(Dimension(STANDINGS_TYRE_WIDTH, STANDINGS_COMPONENT_HEIGHT))
        _ <- time.setPreferredSize(Dimension(STANDINGS_TIME_WIDTH, STANDINGS_COMPONENT_HEIGHT))
        _ <- time.setHorizontalAlignment(SwingConstants.CENTER)
        _ <- fastestLap.setPreferredSize(Dimension(STANDINGS_TIME_WIDTH, STANDINGS_COMPONENT_HEIGHT))
        _ <- fastestLap.setHorizontalAlignment(SwingConstants.CENTER)
        _ <-
          if controller.fastestCar.equals(elem._2.name) then fastestLapIcon.setVisible(true)
          else fastestLapIcon.setVisible(false)
        _ <- p.add(position)
        _ <- p.add(name)
        _ <- p.add(color)
        _ <- p.add(paddingLabel)
        _ <- p.add(img)
        _ <- p.add(paddingLabel1)
        _ <- p.add(tyre)
        _ <- p.add(degradation)
        _ <- p.add(fuel)
        _ <- p.add(time)
        _ <- p.add(fastestLap)
        _ <- p.add(fastestLapIcon)
        _ <- panel.add(p)
      yield ()
      p.runSyncUnsafe()
