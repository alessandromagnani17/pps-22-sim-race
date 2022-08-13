package it.unibo.pps.view.main_panel

import it.unibo.pps.controller.ControllerModule
import it.unibo.pps.model.Tyre
import it.unibo.pps.utility.GivenConversion.GuiConversion.given
import it.unibo.pps.view.main_panel.{CarSelectionPanel, ImageLoader}
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global

import java.awt.event.{ActionEvent, ActionListener, ItemEvent, ItemListener}
import java.awt.*
import java.util
import javax.swing.*

trait CarSelectionPanel extends JPanel:
  def updateDisplayedCar(): Unit

object CarSelectionPanel:
  def apply(width: Int, height: Int, controller: ControllerModule.Controller): CarSelectionPanel =
    CarSelectionPanelImpl(width, height, controller)

  private class CarSelectionPanelImpl(width: Int, height: Int, controller: ControllerModule.Controller)
      extends CarSelectionPanel:
    self =>
    private val imageLoader = ImageLoader()
    private val colorNotSelected = Color(238, 238, 238)
    private val colorSelected = Color(79, 195, 247)
    private val numCars = 4
    private val carNames: Map[Int, String] = Map(0 -> "Ferrari", 1 -> "Mercedes", 2 -> "Red Bull", 3 -> "McLaren")
    private val carSelectedLabel = createLabel(s"Car selected: ${carNames(0)}")
    private val topArrowButton = createArrowButton(
      "/arrows/arrow-up.png",
      e => if (e + 1) == numCars then 0.toString else (e + 1).toString
    )
    private val bottomArrowButton = createArrowButton(
      "/arrows/arrow-bottom.png",
      e => if (e - 1) < 0 then (numCars - 1).toString else (e - 1).toString
    )
    private val labelImage = createLabelImage("/cars/0-hard.png")
    private val carSelectionPanel = createPanelAndAddAllComponents()

    private val positionsButton = createButton("Set up the Starting Positions")

    carSelectionPanel foreach (e => self.add(e))

    def updateDisplayedCar(): Unit =
      labelImage.foreach(e => e.setIcon(imageLoader.load(controller.getCurrentCar().path)))

    private def createLabelImage(filename: String): Task[JLabel] =
      for
        label <- JLabel(imageLoader.load(filename))
        _ <- label.setPreferredSize(Dimension(width, (height * 0.35).toInt))
        _ <- label.setVerticalAlignment(SwingConstants.CENTER)
      yield label

    private def createLabel(text: String): Task[JLabel] =
      for
        label <- JLabel(text)
        _ <- label.setPreferredSize(Dimension(width, (height * 0.2).toInt))
        _ <- label.setVerticalAlignment(SwingConstants.CENTER)
        _ <- label.setHorizontalAlignment(SwingConstants.CENTER)
      yield label

    private def createArrowButton(path: String, calcIndex: Int => String): Task[JButton] =
      for
        button <- JButton(imageLoader.load(path))
        _ <- button.setBorder(BorderFactory.createEmptyBorder())
        _ <- button.setBackground(colorNotSelected)
        _ <- button.setVerticalAlignment(SwingConstants.BOTTOM)
        _ <- button.addActionListener { e =>
          val nextIndex = calcIndex(controller.getCurrentCarIndex())
          controller.setCurrentCarIndex(nextIndex.toInt)
          controller.getCurrentCar().path = s"/cars/$nextIndex-${controller.getCurrentCar().tyre.toString.toLowerCase}.png"
          updateDisplayedCar()
          controller.updateParametersPanel()
          carSelectedLabel.foreach(e => e.setText(s"Car selected: ${carNames(controller.getCurrentCarIndex())}"))
        }
      yield button

    private def createButton(text: String): Task[JButton] =
      for
        button <- JButton(text)
        _ <- button.setPreferredSize(Dimension((width * 0.4).toInt, (height * 0.1).toInt))
        _ <- button.addActionListener(e => controller.displayStartingPositionsPanel())
      yield button

    private def createPanelAndAddAllComponents(): Task[JPanel] =
      for
        panel <- JPanel()
        _ <- panel.setPreferredSize(Dimension(width, height))
        _ <- panel.setLayout(FlowLayout())
        carSelectedLabel <- carSelectedLabel
        topArrowButton <- topArrowButton
        bottomArrowButton <- bottomArrowButton
        labelImage <- labelImage
        positionsButton <- positionsButton
        paddingLabel <- JLabel()
        _ <- paddingLabel.setPreferredSize(Dimension(width, (height * 0.1).toInt))
        _ <- panel.add(carSelectedLabel)
        _ <- panel.add(topArrowButton)
        _ <- panel.add(labelImage)
        _ <- panel.add(bottomArrowButton)
        _ <- panel.add(paddingLabel)
        _ <- panel.add(positionsButton)
        _ <- panel.setVisible(true)
      yield panel
