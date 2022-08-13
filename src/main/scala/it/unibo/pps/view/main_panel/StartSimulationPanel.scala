package it.unibo.pps.view.main_panel

import it.unibo.pps.controller.ControllerModule
import it.unibo.pps.utility.GivenConversion.GuiConversion.given
import it.unibo.pps.view.main_panel.{ImageLoader, StartSimulationPanel}
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global

import java.awt.{Color, Dimension, FlowLayout}
import javax.swing.*

trait StartSimulationPanel extends JPanel

object StartSimulationPanel:
  def apply(width: Int, height: Int, controller: ControllerModule.Controller): StartSimulationPanel =
    StartSimulationPanelImpl(width, height, controller)

  private class StartSimulationPanelImpl(width: Int, height: Int, controller: ControllerModule.Controller)
      extends StartSimulationPanel:
    self =>
    private val imageLoader = ImageLoader()
    private val colorNotSelected = Color(238, 238, 238)
    private val colorSelected = Color(79, 195, 247)
    private var numLaps = 20
    private val lapsLabel =
      createLabel("Select laps:", Dimension((width * 0.06).toInt, (height * 0.06).toInt), SwingConstants.LEFT)
    private val rightArrowButton = createArrowButton("/arrows/arrow-right.png", _ < 50, _ + 1)
    private val leftArrowButton = createArrowButton("/arrows/arrow-left.png", _ > 20, _ - 1)
    private val lapsSelectedLabel =
      createLabel(numLaps.toString, Dimension((width * 0.04).toInt, (height * 0.06).toInt), SwingConstants.CENTER)
    private val startButton = createButton("Start Simulation")
    private val startSimulationPanel = createPanelAndAddAllComponents()

    startSimulationPanel foreach (e => self.add(e))

    private def createButton(text: String): Task[JButton] =
      for
        button <- JButton(text)
        _ <- button.setPreferredSize(Dimension((width * 0.2).toInt, (height * 0.2).toInt))
        _ <- button.addActionListener(e => controller.displaySimulationPanel())
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
        button <- JButton(imageLoader.load(path))
        _ <- button.setBorder(BorderFactory.createEmptyBorder())
        _ <- button.setBackground(colorNotSelected)
        _ <- button.addActionListener(e => {
          if comparator(numLaps) then
            numLaps = function(numLaps)
            lapsSelectedLabel.foreach(e => e.setText(numLaps.toString))
        })
      yield button

    private def createPanelAndAddAllComponents(): Task[JPanel] =
      for
        panel <- JPanel()
        _ <- panel.setPreferredSize(Dimension(width, height))
        _ <- panel.setLayout(FlowLayout())
        lapsLabel <- lapsLabel
        leftArrowButton <- leftArrowButton
        rightArrowButton <- rightArrowButton
        lapsSelectedLabel <- lapsSelectedLabel
        startButton <- startButton
        paddingLabel <- JLabel()
        paddingLabel1 <- JLabel()
        _ <- paddingLabel.setPreferredSize(Dimension(width, (height * 0.03).toInt))
        _ <- paddingLabel1.setPreferredSize(Dimension(width, (height * 0.03).toInt))
        _ <- panel.add(paddingLabel)
        _ <- panel.add(lapsLabel)
        _ <- panel.add(leftArrowButton)
        _ <- panel.add(lapsSelectedLabel)
        _ <- panel.add(rightArrowButton)
        _ <- panel.add(paddingLabel1)
        _ <- panel.add(startButton)
        _ <- panel.setVisible(true)
      yield panel
