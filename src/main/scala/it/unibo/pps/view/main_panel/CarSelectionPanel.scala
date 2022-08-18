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
    private val carSelectedLabel = createLabel(s"Car selected: ${carNames(0)}", Dimension(width, (height * 0.2).toInt), SwingConstants.CENTER, SwingConstants.CENTER, false)
    private val topArrowButton = createArrowButton(
      "/arrows/arrow-up.png",
      e => if (e + 1) == numCars then 0.toString else (e + 1).toString
    )
    private val bottomArrowButton = createArrowButton(
      "/arrows/arrow-bottom.png",
      e => if (e - 1) < 0 then (numCars - 1).toString else (e - 1).toString
    )
    private val labelImage = createLabel("/cars/0-hard.png", Dimension(width, (height * 0.35).toInt), SwingConstants.CENTER, 9, true)
    private val carSelectionPanel = createPanelAndAddAllComponents()

    carSelectionPanel foreach (e => self.add(e))

    def updateDisplayedCar(): Unit =
      labelImage.foreach(e => e.setIcon(imageLoader.load(controller.getCurrentCar().path)))

    private def createLabel(text: String, dimension: Dimension, vertical: Int, horizontal: Int, isImage: Boolean): Task[JLabel] =
      for
        label <- if isImage then JLabel(imageLoader.load(text)) else JLabel(text)
        _ <- label.setPreferredSize(dimension)
        _ <- label.setVerticalAlignment(vertical)
        _ <- if !isImage then label.setHorizontalAlignment(horizontal)
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

    private def createPanelAndAddAllComponents(): Task[JPanel] =
      for
        panel <- JPanel()
        _ <- panel.setPreferredSize(Dimension(width, height))
        _ <- panel.setLayout(FlowLayout())
        carSelectedLabel <- carSelectedLabel
        topArrowButton <- topArrowButton
        bottomArrowButton <- bottomArrowButton
        labelImage <- labelImage
        _ <- panel.add(carSelectedLabel)
        _ <- panel.add(topArrowButton)
        _ <- panel.add(labelImage)
        _ <- panel.add(bottomArrowButton)
        _ <- panel.setVisible(true)
      yield panel