package it.unibo.pps.view

import it.unibo.pps.controller.ControllerModule
import it.unibo.pps.utility.GivenConversion.GuiConversion.given
import monix.eval.Task

import java.awt.{BorderLayout, Color, Component, Dimension, FlowLayout, GridBagConstraints, GridBagLayout, LayoutManager}
import javax.swing.{BorderFactory, DefaultListCellRenderer, ImageIcon, JButton, JComboBox, JLabel, JList, JPanel, SwingConstants}
import javax.swing.JPanel
import monix.execution.Scheduler.Implicits.global

import java.awt.event.{ActionEvent, ActionListener, ItemEvent, ItemListener}
import java.util


trait CarSelectionPanel extends JPanel:
  def changeCar(carIndex: Int, tyresType: String): Unit

object CarSelectionPanel:
  def apply(width: Int, height: Int, controller: ControllerModule.Controller): CarSelectionPanel = CarSelectionPanelImpl(width, height, controller)

  private class CarSelectionPanelImpl (width: Int, height: Int, controller: ControllerModule.Controller)
    extends CarSelectionPanel:
    self =>

    private var currentCarIndex = 0
    private val colorNotSelected = Color(238, 238, 238)
    private val colorSelected = Color(79, 195, 247)
    private val numCars = 4
    private val carNames: Map[Int, String] = Map(0 -> "Ferrari", 1 -> "Mercedes", 2 -> "Red Bull", 3 -> "McLaren")
    private val carSelectedLabel = createJLabel(s"Car selected: ${carNames(currentCarIndex)}")
    private val topArrowButton = createArrowButton("src/main/resources/arrows/arrow-up.png", e => if (e + 1) == numCars then 0.toString else (e + 1).toString)
    private val bottomArrowButton = createArrowButton("src/main/resources/arrows/arrow-bottom.png", e => if (e - 1) < 0 then (numCars - 1).toString else (e - 1).toString)
    private val labelImage = createLabelImage("src/main/resources/cars/0-hard.png", "0")
    private val initialLeftPanel = createPanelAndAddAllComponents()

    controller.setCurrentCarIndex(currentCarIndex)
    initialLeftPanel foreach(e => self.add(e))

    def changeCar(carIndex: Int, tyresType: String): Unit = labelImage.foreach(e => e.setIcon(ImageIcon(s"src/main/resources/cars/$carIndex-$tyresType.png")))

    private def createLabelImage(filename: String, name: String): Task[JLabel] =
      for
        label <- JLabel(ImageIcon(filename))
        _ <- label.setName(name)
        _ <- label.setPreferredSize(Dimension(width, (height * 0.35).toInt))
        _ <- label.setVerticalAlignment(SwingConstants.CENTER)
      yield label
    
    private def createJLabel(text: String): Task[JLabel] =
      for
        label <- JLabel(text)
        _ <- label.setPreferredSize(Dimension(width, (height * 0.2).toInt))
        _ <- label.setVerticalAlignment(SwingConstants.CENTER)
        _ <- label.setHorizontalAlignment(SwingConstants.CENTER)
      yield label

    private def createArrowButton(path: String, calcIndex: Int => String): Task[JButton] =
      for
        button <- JButton(ImageIcon(path))
        _ <- button.setBorder(BorderFactory.createEmptyBorder())
        _ <- button.setBackground(colorNotSelected)
        _ <- button.setVerticalAlignment(SwingConstants.BOTTOM)
        _ <- button.addActionListener(new ActionListener {
          override def actionPerformed(e: ActionEvent): Unit =
            val nextIndex = calcIndex(currentCarIndex)
            controller.setCurrentCarIndex(nextIndex.toInt) 
            changeCar(nextIndex.toInt, "hard")
            currentCarIndex = nextIndex.toInt
            carSelectedLabel.foreach(e => e.setText(s"Car selected: ${carNames(currentCarIndex)}"))
        })
      yield button

    private def createPanelAndAddAllComponents(): Task[JPanel] =
      for
        panel <- JPanel()
        _ <- panel.setPreferredSize(Dimension(width, height))
        _ <- panel.setLayout(FlowLayout())
        carLabel <- carSelectedLabel
        topArrowButton <- topArrowButton
        bottomArrowButton <- bottomArrowButton
        labelImage <- labelImage
        _ <- panel.add(carLabel)
        _ <- panel.add(topArrowButton)
        _ <- panel.add(labelImage)
        _ <- panel.add(bottomArrowButton)
        _ <- panel.setVisible(true)
      yield panel