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


trait InitialLeftPanel extends JPanel:
  def changeCar(carIndex: Int, tyresType: String): Unit

object InitialLeftPanel:
  def apply(width: Int, height: Int, controller: ControllerModule.Controller): InitialLeftPanel = InitialLeftPanelImpl(width, height, controller)

  private class InitialLeftPanelImpl (width: Int, height: Int, controller: ControllerModule.Controller)
    extends InitialLeftPanel:
    self =>

    private var currentCarIndex = 0
    private val mapCarNames: Map[Int, String] = Map(0 -> "Ferrari", 1 -> "Mercedes", 2 -> "Red Bull", 3 -> "McLaren")

    private val initialLeftPanel = createPanel()

    private val carSelectedLabel = createJLabel("Car selected: " + mapCarNames(currentCarIndex))

    private val topArrowButton = createTopArrowButton("src/main/resources/arrows/arrow-up.png")
    private val bottomArrowButton = createBottomArrowButton("src/main/resources/arrows/arrow-bottom.png")

    private val labelImage = createLabelImage("src/main/resources/cars/0-hard.png", "0")
    /*private val labelImage2 = createLabelImage("src/main/resources/cars/1.png", "1")
    private val labelImage3 = createLabelImage("src/main/resources/cars/2.png", "2")
    private val labelImage4 = createLabelImage("src/main/resources/cars/3.png", "3")

    private val labelImages = List(labelImage1, labelImage2, labelImage3, labelImage4)*/

    private val colorNotSelected = Color(238, 238, 238)
    private val colorSelected = Color(79, 195, 247)
    private val numCars = 4

    controller.setCurrentCarIndex(currentCarIndex)

    initialLeftPanel foreach(e => self.add(e))

    def changeCar(carIndex: Int, tyresType: String): Unit = labelImage.foreach(e => e.setIcon(ImageIcon("src/main/resources/cars/" + carIndex + "-" + tyresType + ".png")))

    // Fai vedere la macchina con indice carIndex e le gomme tyresType


    private def createLabelImage(filename: String, name: String): Task[JLabel] =
      for
        label <- JLabel(ImageIcon(filename))
        _ <- label.setName(name)
        _ <- label.setPreferredSize(Dimension(width, (height * 0.4).toInt))
        _ <- label.setVerticalAlignment(SwingConstants.CENTER)
      yield label
    
    private def createJLabel(text: String): Task[JLabel] =
      for
        label <- JLabel(text)
        _ <- label.setPreferredSize(Dimension(width, (height * 0.2).toInt))
        _ <- label.setVerticalAlignment(SwingConstants.CENTER)
        _ <- label.setHorizontalAlignment(SwingConstants.CENTER)
      yield label

    private def createTopArrowButton(filename: String): Task[JButton] =
      for
        button <- JButton(ImageIcon(filename))
        _ <- button.setBackground(colorNotSelected)
        _ <- button.setVerticalAlignment(SwingConstants.BOTTOM)
        _ <- button.addActionListener(new ActionListener {
          override def actionPerformed(e: ActionEvent): Unit =
            val nextIndex = if (currentCarIndex + 1) == numCars then 0.toString else (currentCarIndex + 1).toString
            
            controller.setCurrentCarIndex(nextIndex.toInt)
            changeCar(nextIndex.toInt, "hard")
            //labelImage.foreach(e => e.setIcon(ImageIcon("src/main/resources/cars/" + nextIndex + "-hard.png")))

            currentCarIndex = nextIndex.toInt
            carSelectedLabel.foreach(e => e.setText("Car selected: " + mapCarNames(currentCarIndex)))
        })
      yield button

    private def createBottomArrowButton(filename: String): Task[JButton] =
      for
        button <- JButton(ImageIcon(filename))
        _ <- button.setBackground(colorNotSelected)
        _ <- button.setVerticalAlignment(SwingConstants.TOP)
        _ <- button.addActionListener(new ActionListener {
          override def actionPerformed(e: ActionEvent): Unit =
            val prevIndex = if (currentCarIndex - 1) < 0 then (numCars - 1).toString else (currentCarIndex - 1).toString
            
            controller.setCurrentCarIndex(prevIndex.toInt)
            changeCar(prevIndex.toInt, "hard")
            //labelImage.foreach(e => e.setIcon(ImageIcon("src/main/resources/cars/" + prevIndex + ".png")))
            currentCarIndex = prevIndex.toInt
            carSelectedLabel.foreach(e => e.setText("Car selected: " + mapCarNames(currentCarIndex)))
        })
      yield button


    private def createPanel(): Task[JPanel] =
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