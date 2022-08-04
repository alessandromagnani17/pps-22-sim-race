package it.unibo.pps.view

import it.unibo.pps.controller.ControllerModule
import it.unibo.pps.utility.GivenConversion.GuiConversion.given
import monix.eval.Task

import java.awt.{BorderLayout, Color, Component, Dimension, FlowLayout, GridBagConstraints, GridBagLayout, LayoutManager}
import javax.swing.{BorderFactory, DefaultListCellRenderer, ImageIcon, JButton, JComboBox, JLabel, JList, JPanel, SwingConstants}
import javax.swing.JPanel
import monix.execution.Scheduler.Implicits.global

import java.awt.event.{ActionEvent, ActionListener, ItemEvent, ItemListener}

trait InitialLeftPanel extends JPanel

object InitialLeftPanel:
  def apply(width: Int, height: Int, controller: ControllerModule.Controller): InitialLeftPanel = InitialLeftPanelImpl(width, height, controller)

  private class InitialLeftPanelImpl (width: Int, height: Int, controller: ControllerModule.Controller)
    extends InitialLeftPanel:
    self =>

    private val initialLeftPanel = createPanel()

    private val topArrowButton = createTopArrowButton("src/main/resources/arrows/arrow-up.png")
    private val bottomArrowButton = createBottomArrowButton("src/main/resources/arrows/arrow-bottom.png")

    private val labelImage1 = createLabelImage("src/main/resources/cars/1.png", "1")
    private val labelImage2 = createLabelImage("src/main/resources/cars/2.png", "2")
    private val labelImage3 = createLabelImage("src/main/resources/cars/3.png", "3")
    private val labelImage4 = createLabelImage("src/main/resources/cars/4.png", "4")

    private val labelImages = List(labelImage1, labelImage2, labelImage3, labelImage4)

    private val colorNotSelected = Color(238, 238, 238)
    private val colorSelected = Color(79, 195, 247)

    initialLeftPanel foreach(e => self.add(e))




    private def createLabelImage(filename: String, name: String): Task[JLabel] =
      for
        label <- JLabel(ImageIcon(filename))
        _ <- label.setVisible(false)
        _ <- label.setName(name)
        _ <- label.setPreferredSize(Dimension(width, (height * 0.4).toInt))
        _ <- label.setVerticalAlignment(SwingConstants.CENTER)
      yield label
    
    private def createJLabel(text: String): Task[JLabel] =
      for
        label <- JLabel(text)
        _ <- label.setPreferredSize(Dimension(width, (height * 0.2).toInt))
        _ <- label.setVerticalAlignment(SwingConstants.TOP)
        _ <- label.setHorizontalAlignment(SwingConstants.CENTER)
      yield label


    private def createTopArrowButton(filename: String): Task[JButton] =
      for
        button <- JButton(ImageIcon(filename))
        _ <- button.setBackground(colorNotSelected)
        _ <- button.setVerticalAlignment(SwingConstants.BOTTOM)
        _ <- button.addActionListener(new ActionListener {
          override def actionPerformed(e: ActionEvent): Unit = labelImages.foreach(r => r.foreach(f => if f.isVisible then println(f.getComponent(1))))
        })
      yield button

    private def createBottomArrowButton(filename: String): Task[JButton] =
      for
        button <- JButton(ImageIcon(filename))
        _ <- button.setBackground(colorNotSelected)
        _ <- button.setVerticalAlignment(SwingConstants.TOP)
        _ <- button.addActionListener(new ActionListener {
          override def actionPerformed(e: ActionEvent): Unit = labelImages.foreach(r => r.foreach(f => if f.isVisible then println(f.getComponent(1))))
        })
      yield button


    private def createPanel(): Task[JPanel] =
      for
        panel <- JPanel()
        _ <- panel.setPreferredSize(Dimension(width, height))
        _ <- panel.setLayout(FlowLayout())

        topArrowButton <- topArrowButton
        bottomArrowButton <- bottomArrowButton

        labelImage1 <- labelImage1
        labelImage2 <- labelImage2
        labelImage3 <- labelImage3
        labelImage4 <- labelImage4
        _ <- labelImage1.setVisible(true)

        _ <- panel.add(topArrowButton)
        _ <- panel.add(labelImage1)
        _ <- panel.add(labelImage2)
        _ <- panel.add(labelImage3)
        _ <- panel.add(labelImage4)
        _ <- panel.add(bottomArrowButton)


        _ <- panel.setVisible(true)
      yield panel