package it.unibo.pps.view.simulation_panel

import it.unibo.pps.controller.ControllerModule
import monix.eval.Task

import javax.swing.{BorderFactory, JButton, JLabel, JPanel, SwingConstants}
import monix.execution.Scheduler.Implicits.global

import java.awt.{BorderLayout, Color, Dimension, FlowLayout}
import scala.collection.mutable.Map
import it.unibo.pps.model.Car
import it.unibo.pps.view.main_panel.ImageLoader

trait EndRacePanel extends JPanel

object EndRacePanel:
  def apply(width: Int, height: Int, controller: ControllerModule.Controller): EndRacePanel =
    EndRacePanelImpl(width, height, controller)

  private class EndRacePanelImpl(width: Int, height: Int, controller: ControllerModule.Controller)
    extends EndRacePanel:
    self =>

    import it.unibo.pps.utility.GivenConversion.GuiConversion.given

    private val imageLoader = ImageLoader()
    private val standingsPanel = createStandingsPanel()
    private val mainPanel = createPanelAndAddAllComponents()

    mainPanel foreach (e => self.add(e))

    private def createStandingsPanel(): Task[JPanel] =
      for
        panel <- JPanel()
        _ <- panel.setPreferredSize(Dimension(600, 400))
        _ <- panel.setBackground(Color.CYAN)
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
        _ <- Map.from(controller.standings._standing.zipWithIndex.map{ case (k,v) => (v,k) }).foreach(e => addToPanel(e, standingsPanel))
        //_ <- controller.standings._standing.foreach(e => addToPanel(e, standingsPanel))
        _ <- panel.add(titleLabel)
        _ <- panel.add(standingsPanel)
        _ <- panel.setVisible(true)
      yield panel

    // Posizione - Nome - Colore - Immagine - Gomme - Tempo
    private def addToPanel(elem: (Int, Car), panel: JPanel): Unit =
      val p = for
        p <- JPanel()
        _ <- p.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.BLACK))
        position <- JLabel(elem._1.toString)
        name <- JLabel(elem._2.name)
        color <- JLabel()
        img <- JLabel(imageLoader.load(s"/cars/miniatures/${elem._1}.png"))
        tyre <- JLabel(elem._2.tyre.toString)
        time <- JLabel(controller.totalLaps.toString)
        _ <- position.setPreferredSize(Dimension(70, 70))
        _ <- name.setPreferredSize(Dimension(70, 70))
        _ <- color.setPreferredSize(Dimension(70, 70))
        _ <- color.setBackground(elem._2.drawingCarParams.color)
        _ <- tyre.setPreferredSize(Dimension(70, 70))
        _ <- time.setPreferredSize(Dimension(70, 70))

        _ <- p.add(position)
        _ <- p.add(name)
        _ <- p.add(color)
        _ <- p.add(img)
        _ <- p.add(tyre)
        _ <- p.add(time)

        _ <- panel.add(p)
      yield ()
      p.runSyncUnsafe()

