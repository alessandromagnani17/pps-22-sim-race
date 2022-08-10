package it.unibo.pps.view

import it.unibo.pps.controller.ControllerModule
import it.unibo.pps.model.Tyre
import it.unibo.pps.utility.GivenConversion.GuiConversion.given
import monix.eval.Task

import java.awt.{BorderLayout, Color, Component, Dimension, FlowLayout, GridBagConstraints, GridBagLayout, LayoutManager}
import javax.swing.{BorderFactory, DefaultListCellRenderer, ImageIcon, JButton, JComboBox, JLabel, JList, JPanel, SwingConstants}
import javax.swing.JPanel
import monix.execution.Scheduler.Implicits.global

import java.awt.event.{ActionEvent, ActionListener, ItemEvent, ItemListener}
import java.util

trait CarSelectionPanel extends JPanel:
  def updateDisplayedCar(carIndex: Int, tyre: Tyre): Unit

object CarSelectionPanel:
  def apply(width: Int, height: Int, controller: ControllerModule.Controller): CarSelectionPanel =
    CarSelectionPanelImpl(width, height, controller)

  private class CarSelectionPanelImpl(width: Int, height: Int, controller: ControllerModule.Controller)
      extends CarSelectionPanel:
    self =>
    private val imageLoader = ImageLoader()
    private var currentCarIndex = 0
    private val colorNotSelected = Color(238, 238, 238)
    private val colorSelected = Color(79, 195, 247)
    private val numCars = 4
    private val carNames: Map[Int, String] = Map(0 -> "Ferrari", 1 -> "Mercedes", 2 -> "Red Bull", 3 -> "McLaren")
    private val carSelectedLabel = createLabel(s"Car selected: ${carNames(currentCarIndex)}")
    private val topArrowButton = createArrowButton(
      "/arrows/arrow-up.png",
      e => if (e + 1) == numCars then 0.toString else (e + 1).toString
    )
    private val bottomArrowButton = createArrowButton(
      "/arrows/arrow-bottom.png",
      e => if (e - 1) < 0 then (numCars - 1).toString else (e - 1).toString
    )
    private val labelImage = createLabelImage("/cars/0-hard.png", "0")
    private val carSelectionPanel = createPanelAndAddAllComponents()

    controller.setCurrentCarIndex(currentCarIndex)
    carSelectionPanel foreach (e => self.add(e))

    def updateDisplayedCar(carIndex: Int, tyre: Tyre): Unit =
      labelImage.foreach(e => e.setIcon(imageLoader.load(s"/cars/$carIndex-${tyre.toString.toLowerCase}.png")))

    private def createLabelImage(filename: String, name: String): Task[JLabel] =
      for
        label <- JLabel(imageLoader.load(filename))
        _ <- label.setName(name)
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
          val nextIndex = calcIndex(currentCarIndex)
          controller.setCurrentCarIndex(nextIndex.toInt)
          updateDisplayedCar(nextIndex.toInt, controller.getCurrentCar().tyre)
          controller.updateParametersPanel()
          currentCarIndex = nextIndex.toInt
          println("Indice car preso dal name -->" + labelImage.foreach(e => println("è+++" + e.getName)))
          carSelectedLabel.foreach(e => e.setText(s"Car selected: ${carNames(currentCarIndex)}"))
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
