package it.unibo.pps.view

import it.unibo.pps.controller.ControllerModule
import it.unibo.pps.utility.GivenConversion.GuiConversion.given
import monix.eval.Task

import java.awt.{BorderLayout, Color, Component, Dimension, FlowLayout, GridBagConstraints, GridBagLayout, LayoutManager}
import javax.swing.{BorderFactory, DefaultListCellRenderer, ImageIcon, JButton, JComboBox, JLabel, JList, JPanel, SwingConstants}
import javax.swing.JPanel
import monix.execution.Scheduler.Implicits.global

import java.awt.event.{ItemEvent, ItemListener}

trait InitialLeftPanel extends JPanel

object InitialLeftPanel:
  def apply(width: Int, height: Int, controller: ControllerModule.Controller): InitialLeftPanel = InitialLeftPanelImpl(width, height, controller)

  private class InitialLeftPanelImpl (width: Int, height: Int, controller: ControllerModule.Controller)
    extends InitialLeftPanel:
    self =>

    private val labelComboBox = createJLabel("Select a car: ")
    private val comboBox = createJComboBox(List("1", "2", "3", "4"))
    private val initialLeftPanel = createPanel()
    private val labelImage1 = createLabelImage("src/main/resources/cars/1.png", "1")
    private val labelImage2 = createLabelImage("src/main/resources/cars/2.png", "2")
    private val labelImage3 = createLabelImage("src/main/resources/cars/3.png", "3")
    private val labelImage4 = createLabelImage("src/main/resources/cars/4.png", "4")

    private val labelImages = List(labelImage1, labelImage2, labelImage3, labelImage4)

    initialLeftPanel foreach(e => self.add(e))




    private def createLabelImage(filename: String, name: String): Task[JLabel] =
      for
        label <- JLabel(ImageIcon(filename))
        _ <- label.setVisible(false)
        _ <- label.setName(name)
        _ <- label.setPreferredSize(Dimension(width, (height * 0.6).toInt))
        _ <- label.setVerticalAlignment(SwingConstants.BOTTOM)
      yield label
    
    private def createJLabel(text: String): Task[JLabel] =
      for
        jl <- JLabel(text)
      yield jl

    private def createJComboBox(options: List[String]): Task[JComboBox[String]] =
      for
        cb <- JComboBox[String]()
        _ <- cb.setRenderer(new DefaultListCellRenderer(){
          override def getListCellRendererComponent(list: JList[_], value: Any, index: Int, isSelected: Boolean, cellHasFocus: Boolean): Component =
            val x = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus)
            if isSelected then
              x.setForeground(Color.WHITE)
              x.setBackground(Color.BLUE)
            else
              x.setForeground(Color.BLUE)
              x.setBackground(Color.WHITE)
            x
        })
        _ <- options foreach (e => cb.addItem(e))
        _ <- cb.setSelectedIndex(-1)
        _ <- cb.setPreferredSize(Dimension(70, 23))
        _ <- cb.setForeground(Color.BLACK)
        _ <- cb.addItemListener(new ItemListener {
          override def itemStateChanged(e: ItemEvent): Unit =
            if e.getStateChange == ItemEvent.SELECTED then { println("Car selected: " + e.getItem);  labelImages.foreach(r => r.foreach(f => {f.setVisible(false); if f.getName == e.getItem then f.setVisible(true)})) }
        })
        _ <- cb.setOpaque(true)
      yield cb


    private def createPanel(): Task[JPanel] =
      for
        panel <- JPanel()
        _ <- panel.setPreferredSize(Dimension(width, height))
        _ <- panel.setLayout(FlowLayout())
        label <- labelComboBox
        comboBox <- comboBox
        labelImage1 <- labelImage1
        labelImage2 <- labelImage2
        labelImage3 <- labelImage3
        labelImage4 <- labelImage4

        _ <- panel.add(label)
        _ <- panel.add(comboBox)

        _ <- panel.add(labelImage1)
        _ <- panel.add(labelImage2)
        _ <- panel.add(labelImage3)
        _ <- panel.add(labelImage4)


        _ <- panel.setVisible(true)
      yield panel