package it.unibo.pps.view.main_panel

import it.unibo.pps.controller.ControllerModule
import it.unibo.pps.model.Tyre
import it.unibo.pps.utility.GivenConversion.GuiConversion.given
import it.unibo.pps.view.Constants.CarSelectionPanelConstants.*
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

    // TODO VEDERE SE LASCIARE width e height passati o se usare solo costanti

    private val carSelectedLabel = createLabel(
      s"Car selected: ${CAR_NAMES(0)}",
      Dimension(width, CAR_SELECTED_HEIGHT),
      SwingConstants.CENTER,
      SwingConstants.CENTER,
      false
    )
    private val topArrowButton = createArrowButton(
      "/arrows/arrow-up.png",
      e => if (e + 1) == NUM_CARS then 0.toString else (e + 1).toString
    )
    private val bottomArrowButton = createArrowButton(
      "/arrows/arrow-bottom.png",
      e => if (e - 1) < 0 then (NUM_CARS - 1).toString else (e - 1).toString
    )
    private val labelImage = createLabel("/cars/0-soft.png", Dimension(width, CAR_IMAGE_HEIGHT), SwingConstants.CENTER, SwingConstants.CENTER, true)
    private val carSelectionPanel = createPanelAndAddAllComponents()

    carSelectionPanel foreach (e => self.add(e))

    def updateDisplayedCar(): Unit =
      labelImage.foreach(e => e.setIcon(ImageLoader.load(controller.currentCar.path)))

    private def createLabel(
        text: String,
        dimension: Dimension,
        vertical: Int,
        horizontal: Int,
        isImage: Boolean
    ): Task[JLabel] =
      for
        label <- if isImage then JLabel(ImageLoader.load(text)) else JLabel(text)
        _ <- label.setPreferredSize(dimension)
        _ <- label.setVerticalAlignment(vertical)
        _ <- if !isImage then label.setHorizontalAlignment(horizontal)
      yield label

    private def createArrowButton(path: String, calcIndex: Int => String): Task[JButton] =
      for
        button <- JButton(ImageLoader.load(path))
        _ <- button.setBorder(BorderFactory.createEmptyBorder())
        _ <- button.setBackground(BUTTON_NOT_SELECTED_COLOR)
        _ <- button.setVerticalAlignment(SwingConstants.BOTTOM)
        _ <- button.addActionListener { e =>
          val nextIndex = calcIndex(controller.currentCarIndex)
          controller.currentCarIndex = nextIndex.toInt
          controller.currentCar.path = s"/cars/$nextIndex-${controller.currentCar.tyre.toString.toLowerCase}.png"
          updateDisplayedCar()
          controller.updateParametersPanel()
          carSelectedLabel.foreach(e => e.setText(s"Car selected: ${CAR_NAMES(controller.currentCarIndex)}"))
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
