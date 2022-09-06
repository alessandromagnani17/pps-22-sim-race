package it.unibo.pps.view.main_panel

import it.unibo.pps.controller.ControllerModule
import it.unibo.pps.utility.GivenConversion.GuiConversion.given
import it.unibo.pps.view.main_panel.StartingPositionsPanel
import it.unibo.pps.view.Constants.StartingPositionsPanelConstants.*
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import scala.collection.mutable.Map

import java.awt.{BorderLayout, Color, Dimension, FlowLayout, GridBagConstraints, GridBagLayout}
import javax.swing.*

trait StartingPositionsPanel extends JPanel

object StartingPositionsPanel:
  def apply(controller: ControllerModule.Controller): StartingPositionsPanel =
    StartingPositionsPanelImpl(controller)

  private class StartingPositionsPanelImpl(controller: ControllerModule.Controller)
      extends StartingPositionsPanel:
    self =>

    private val topLabel = createLabel(
      Dimension(STARTING_POS_PANEL_WIDTH, TOP_LABEL_HEIGHT),
      SwingConstants.CENTER,
      () => Left("Sets the order of the starting grid: ")
    )
    private val positionPanel = createPanel()
    private val positions = createStartingPositions()
    private val startingPositionsPanel = createPanelAndAddAllComponents()

    startingPositionsPanel foreach (e => self.add(e))

    private def createStartingPositions(): List[(Task[JLabel], Task[JLabel], Task[JLabel], Task[JButton], Task[JButton])] =
      var l: List[(Task[JLabel], Task[JLabel], Task[JLabel], Task[JButton], Task[JButton])] = List.empty
      for i <- 0 until NUM_CARS do
        l = l :+ ((createLabel(
          Dimension(CAR_MINIATURE_WIDTH, CAR_MINIATURE_HEIGHT),
          SwingConstants.CENTER,
          () => Right(ImageLoader.load(s"/cars/miniatures/$i.png"))
        ),
        createLabel(Dimension(CAR_POS_WIDTH, CAR_POS_HEIGHT), SwingConstants.LEFT, () => Left(s"${i + 1}. ")),
        createLabel(Dimension(CAR_NAME_WIDTH, CAR_POS_HEIGHT), SwingConstants.LEFT, () => Left(s"${CAR_NAMES(i)}")),
        if i == 0 then createButton(i, "/arrows/blank_background.png", e => if e == 0 then e else e - 1)
        else createButton(i, "/arrows/arrow-up.png", e => if e == 0 then e else e - 1),
        if i == (NUM_CARS - 1) then createButton(i, "/arrows/blank_background.png", e => if e == 0 then e else e - 1)
        else createButton(i, "/arrows/arrow-bottom.png", e => if e == (NUM_CARS - 1) then e else e + 1)))
      l

    private def createLabel(dim: Dimension, horizontal: Int, f: () => Either[String, ImageIcon]): Task[JLabel] =
      for
        label <- f() match
          case Left(s: String) => JLabel(s)
          case Right(i: ImageIcon) => JLabel(i)
        _ <- label.setPreferredSize(dim)
        _ <- label.setHorizontalAlignment(horizontal)
      yield label

    private def createButton(index: Int, path: String, calcIndex: Int => Int): Task[JButton] =
      for
        button <- JButton(ImageLoader.load(path))
        _ <- button.setBorder(BorderFactory.createEmptyBorder())
        _ <- button.setBackground(BUTTON_NOT_SELECTED_COLOR)
        _ <- button.setHorizontalAlignment(SwingConstants.RIGHT)
        _ <- button.addActionListener { e =>
          val nextIndex = calcIndex(index)
          controller.invertPosition(index, nextIndex)
          invertParams(index, nextIndex)
        }
      yield button

    private def invertParams(prevIndex: Int, nextIndex: Int): Unit =
      var nextLabelSupport = ""
      var prevLabelSupport = ""

      val p = for
        nextLabel <- positions(nextIndex)._3
        nextLabelSupport = nextLabel.getText
        prevLabel <- positions(prevIndex)._3
        prevLabelSupport = prevLabel.getText
        nextImage <- positions(nextIndex)._1
        prevImage <- positions(prevIndex)._1
        _ <- nextLabel.setText(prevLabelSupport)
        _ <- prevLabel.setText(nextLabelSupport)
        _ <- nextImage.setIcon(
          ImageLoader.load(s"/cars/miniatures/${CAR_NAMES.find(_._2.equals(prevLabelSupport)).get._1}.png")
        )
        _ <- prevImage.setIcon(
          ImageLoader.load(s"/cars/miniatures/${CAR_NAMES.find(_._2.equals(nextLabelSupport)).get._1}.png")
        )
      yield ()
      p.runSyncUnsafe()

    private def createPanel(): Task[JPanel] =
      for
        panel <- JPanel()
        _ <- panel.setPreferredSize(Dimension(STARTING_POS_PANEL_WIDTH, STARTING_POS_SUBPANEL_HEIGHT))
      yield panel

    private def createPanelAndAddAllComponents(): Task[JPanel] =
      for
        panel <- JPanel()
        _ <- panel.setPreferredSize(Dimension(STARTING_POS_PANEL_WIDTH, STARTING_POS_PANEL_HEIGHT))
        topLabel <- topLabel
        positionPanel <- positionPanel
        _ <- positions.foreach(e => addToPanel(e, positionPanel))
        _ <- panel.add(topLabel)
        _ <- panel.add(positionPanel)
        _ <- panel.setVisible(true)
      yield panel

    private def addToPanel(
        elem: (Task[JLabel], Task[JLabel], Task[JLabel], Task[JButton], Task[JButton]),
        posPanel: JPanel
    ): Task[Unit] =
      val p = for
        panel <- JPanel()
        _ <- panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.BLACK))
        img <- elem._1
        pos <- elem._2
        name <- elem._3
        upButton <- elem._4
        downButton <- elem._5
        blank <- JLabel(ImageLoader.load("/arrows/blank_background.png"))
        _ <- panel.add(pos)
        _ <- panel.add(name)
        _ <- panel.add(img)
        _ <- if pos.getText.equals("4. ") then panel.add(blank)
        _ <- panel.add(upButton)
        _ <- panel.add(downButton)
        _ <- if pos.getText.equals("1. ") then panel.add(blank)
        _ <- posPanel.add(panel)
      yield ()
      p.runSyncUnsafe()
