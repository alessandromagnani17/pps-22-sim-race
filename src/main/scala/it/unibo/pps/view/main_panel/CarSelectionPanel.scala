package it.unibo.pps.view.main_panel

import it.unibo.pps.controller.ControllerModule
import it.unibo.pps.model.Tyre
import it.unibo.pps.utility.GivenConversion.GuiConversion.given
import it.unibo.pps.view.Constants.CarSelectionPanelConstants.*
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import it.unibo.pps.utility.PimpScala.RichJPanel.*

import java.awt.event.{ActionEvent, ActionListener, ItemEvent, ItemListener}
import java.awt.*
import java.util
import javax.swing.*

trait CarSelectionPanel extends JPanel:

  /** Method that updates the car displayed */
  def updateDisplayedCar(): Unit

object CarSelectionPanel:
  def apply(controller: ControllerModule.Controller): CarSelectionPanel =
    CarSelectionPanelImpl(controller)

  private class CarSelectionPanelImpl(controller: ControllerModule.Controller) extends CarSelectionPanel:
    self =>

    private val carSelectedLabel = createLabel(
      Dimension(SELECTION_PANEL_WIDTH, CAR_SELECTED_HEIGHT),
      SwingConstants.CENTER,
      SwingConstants.CENTER,
      () => Left(s"Car selected: ${CAR_NAMES(0)}")
    )
    private val topArrowButton = createArrowButton(
      "/arrows/arrow-up.png",
      e => if (e + 1) == NUM_CARS then 0.toString else (e + 1).toString
    )
    private val bottomArrowButton = createArrowButton(
      "/arrows/arrow-bottom.png",
      e => if (e - 1) < 0 then (NUM_CARS - 1).toString else (e - 1).toString
    )
    private val labelImage = createLabel(
      Dimension(SELECTION_PANEL_WIDTH, CAR_IMAGE_HEIGHT),
      SwingConstants.CENTER,
      SwingConstants.CENTER,
      () => Right(ImageLoader.load("/cars/0-soft.png"))
    )
    private val carSelectionPanel = createPanelAndAddAllComponents()

    carSelectionPanel foreach (e => self.add(e))

    def updateDisplayedCar(): Unit =
      labelImage.foreach(e => e.setIcon(ImageLoader.load(controller.currentCar.path)))

    private def createLabel(
        dim: Dimension,
        vertical: Int,
        horizontal: Int,
        f: () => Either[String, ImageIcon]
    ): Task[JLabel] =
      for
        label <- f() match
          case Left(s: String) => JLabel(s)
          case Right(i: ImageIcon) => JLabel(i)
        _ <- label.setPreferredSize(dim)
        _ <- label.setVerticalAlignment(vertical)
        _ <- label.setHorizontalAlignment(horizontal)
      yield label

    private def createArrowButton(path: String, calcIndex: Int => String): Task[JButton] =
      for
        button <- JButton(ImageLoader.load(path))
        _ <- button.setBorder(BorderFactory.createEmptyBorder())
        _ <- button.setBackground(BUTTON_NOT_SELECTED_COLOR)
        _ <- button.setVerticalAlignment(SwingConstants.BOTTOM)
        _ <- button.addActionListener { e =>
          controller.updateCurrentCarIndex(calcIndex)
          updateDisplayedCar()
          controller.updateParametersPanel
          carSelectedLabel.foreach(e => e.setText(s"Car selected: ${CAR_NAMES(controller.currentCarIndex)}"))
        }
      yield button

    private def createPanelAndAddAllComponents(): Task[JPanel] =
      for
        panel <- JPanel()
        _ <- panel.setPreferredSize(Dimension(SELECTION_PANEL_WIDTH, SELECTION_PANEL_HEIGHT))
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
