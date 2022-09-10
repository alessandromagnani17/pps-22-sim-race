package it.unibo.pps.view.main_panel

import it.unibo.pps.controller.ControllerModule
import it.unibo.pps.utility.GivenConversion.GuiConversion.given
import it.unibo.pps.view.Constants.StartSimulationPanelConstants.*
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global

import java.awt.{Color, Dimension, FlowLayout}
import javax.swing.*

trait StartSimulationPanel extends JPanel

object StartSimulationPanel:
  def apply(controller: ControllerModule.Controller): StartSimulationPanel =
    StartSimulationPanelImpl(controller)

  private class StartSimulationPanelImpl(controller: ControllerModule.Controller) extends StartSimulationPanel:
    self =>
    private val lapsLabel =
      createLabel("Select laps:", Dimension(LAPS_LABEL_WIDTH, LAPS_LABEL_HEIGHT), SwingConstants.LEFT)
    private val rightArrowButton = createArrowButton("/arrows/arrow-right.png", _ < MAX_LAPS, _ + 1)
    private val leftArrowButton = createArrowButton("/arrows/arrow-left.png", _ > MIN_LAPS, _ - 1)
    private val lapsSelectedLabel =
      createLabel(
        controller.totalLaps.toString,
        Dimension(LAPS_SELECTED_LABEL_WIDTH, LAPS_LABEL_HEIGHT),
        SwingConstants.CENTER
      )
    private val startingPositionsButton = createButton(
      "Set up the Starting Positions",
      () => controller.displayStartingPositionsPanel
    )
    private val startButton = createButton(
      "Start Simulation",
      () => controller.displaySimulationPanel
    )
    private val startSimulationPanel = createPanelAndAddAllComponents()

    startSimulationPanel foreach (e => self.add(e))

    private def createButton(text: String, action: () => Unit): Task[JButton] =
      for
        button <- JButton(text)
        _ <- button.setPreferredSize(Dimension(BUTTONS_WIDTH, BUTTONS_HEIGHT))
        _ <- button.addActionListener(e => action())
      yield button

    private def createLabel(text: String, dim: Dimension, pos: Int): Task[JLabel] =
      for
        label <- JLabel(text)
        _ <- label.setVerticalAlignment(SwingConstants.CENTER)
        _ <- label.setPreferredSize(dim)
        _ <- label.setHorizontalAlignment(pos)
      yield label

    private def createArrowButton(path: String, comparator: Int => Boolean, function: Int => Int): Task[JButton] =
      for
        button <- JButton(ImageLoader.load(path))
        _ <- button.setBorder(BorderFactory.createEmptyBorder())
        _ <- button.setBackground(BUTTON_NOT_SELECTED_COLOR)
        _ <- button.addActionListener(e => {
          if comparator(controller.totalLaps) then
            controller.totalLaps = function(controller.totalLaps)
            lapsSelectedLabel.foreach(e => e.setText(controller.totalLaps.toString))
        })
      yield button

    private def createPanelAndAddAllComponents(): Task[JPanel] =
      for
        panel <- JPanel()
        _ <- panel.setPreferredSize(Dimension(FRAME_WIDTH, START_PANEL_HEIGHT))
        _ <- panel.setLayout(FlowLayout())
        lapsLabel <- lapsLabel
        leftArrowButton <- leftArrowButton
        rightArrowButton <- rightArrowButton
        lapsSelectedLabel <- lapsSelectedLabel
        startingPositionsButton <- startingPositionsButton
        startButton <- startButton
        paddingLabel <- JLabel()
        paddingLabel1 <- JLabel()
        paddingLabel2 <- JLabel()
        _ <- paddingLabel.setPreferredSize(Dimension(FRAME_WIDTH, PADDING_LABEL_HEIGHT))
        _ <- paddingLabel1.setPreferredSize(Dimension(FRAME_WIDTH, PADDING_LABEL_HEIGHT))
        _ <- paddingLabel2.setPreferredSize(Dimension(FRAME_WIDTH, PADDING_LABEL_HEIGHT1))
        _ <- panel.add(paddingLabel)
        _ <- panel.add(lapsLabel)
        _ <- panel.add(leftArrowButton)
        _ <- panel.add(lapsSelectedLabel)
        _ <- panel.add(rightArrowButton)
        _ <- panel.add(paddingLabel1)
        _ <- panel.add(startingPositionsButton)
        _ <- panel.add(paddingLabel2)
        _ <- panel.add(startButton)
        _ <- panel.setVisible(true)
      yield panel
