package it.unibo.pps.view.simulation_panel

import it.unibo.pps.controller.ControllerModule
import monix.eval.Task

import javax.swing.{BorderFactory, JButton, JLabel, JPanel, SwingConstants}
import monix.execution.Scheduler.Implicits.global

import java.awt.{BorderLayout, Color, Dimension, FlowLayout}
import scala.collection.mutable.Map
import it.unibo.pps.model.Car
import it.unibo.pps.view.main_panel.ImageLoader
import it.unibo.pps.view.ViewConstants.*

trait EndRacePanel extends JPanel

object EndRacePanel:
  def apply(width: Int, height: Int, controller: ControllerModule.Controller): EndRacePanel =
    EndRacePanelImpl(width, height, controller)

  private class EndRacePanelImpl(width: Int, height: Int, controller: ControllerModule.Controller) extends EndRacePanel:
    self =>

    import it.unibo.pps.utility.GivenConversion.GuiConversion.given

    private val standingsPanel = createStandingsPanel()
    private val mainPanel = createPanelAndAddAllComponents()

    mainPanel foreach (e => self.add(e))

    private def createStandingsPanel(): Task[JPanel] =
      for
        panel <- JPanel()
        _ <- panel.setPreferredSize(Dimension(900, 400))
      yield panel

    private def createPanelAndAddAllComponents(): Task[JPanel] =
      for
        panel <- JPanel()
        _ <- panel.setPreferredSize(Dimension(width, height))
        titleLabel <- JLabel("Final Standings:")
        _ <- titleLabel.setPreferredSize(Dimension(width, 150))
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
        /*<<<<<<< HEAD
        _ <- color.setBackground(elem._2.drawingCarParams.color)
        _ <- color.setOpaque(true)
        img <- JLabel(ImageLoader.load(s"/cars/miniatures/${CAR_NAMES.find(_._2.equals(elem._2.name)).get._1}.png"))
=======*/
        _ <- color.setBackground(elem._2.drawingCarParams.color)
        _ <- color.setOpaque(true)
        img <- JLabel(ImageLoader.load(s"/cars/miniatures/${CAR_NAMES.find(_._2.equals(elem._2.name)).get._1}.png"))
//>>>>>>> ce4c801a6798ee4ef0ee6dc6d79283477e2d753e
        tyre <- JLabel(elem._2.tyre.toString)
        time <- JLabel(controller.calcCarPosting(elem._2))
        fastestLap <- JLabel(controller.convertTimeToMinutes(elem._2.fastestLap))
        fastestLapIcon <- JLabel(ImageLoader.load("/fastest-lap-logo.png"))
        _ <- position.setPreferredSize(Dimension(20, 70))
        _ <- name.setPreferredSize(Dimension(100, 70))
        _ <- color.setPreferredSize(Dimension(20, 70))
        _ <- color.setBackground(elem._2.drawingCarParams.color)
        _ <- color.setOpaque(true)
        _ <- tyre.setPreferredSize(Dimension(120, 70))
        _ <- fastestLap.setPreferredSize(Dimension(90, 70))
        _ <- time.setPreferredSize(Dimension(90, 70))
        _ <-
          if controller.fastestCar.equals(elem._2.name) then fastestLapIcon.setVisible(true)
          else fastestLapIcon.setVisible(false)
        _ <- p.add(position)
        _ <- p.add(name)
        _ <- p.add(color)
        _ <- p.add(img)
        _ <- p.add(tyre)
        _ <- p.add(time)
        _ <- p.add(fastestLap)
        _ <- p.add(fastestLapIcon)
        _ <- panel.add(p)
      yield ()
      p.runSyncUnsafe()
