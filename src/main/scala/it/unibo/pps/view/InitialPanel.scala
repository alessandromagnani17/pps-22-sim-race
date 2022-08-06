package it.unibo.pps.view

import it.unibo.pps.controller.ControllerModule
import it.unibo.pps.utility.GivenConversion.GuiConversion.given
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global

import java.io.File
import java.awt.event.{ActionEvent, ActionListener, ItemEvent, ItemListener}
import java.awt.image.BufferedImage
import java.awt.{BorderLayout, Color, Component, Dimension, FlowLayout, GridBagConstraints, GridBagLayout, LayoutManager}
import javax.imageio.ImageIO
import javax.swing.{BorderFactory, DefaultListCellRenderer, ImageIcon, JButton, JComboBox, JLabel, JList, JPanel, SwingConstants}

trait InitialPanel extends JPanel:
  def changeCar(carIndex: Int, tyresType: String): Unit

object InitialPanel:
  def apply(width: Int, height: Int, controller: ControllerModule.Controller): InitialPanel = InitialPanelImpl(width, height, controller)

  private class InitialPanelImpl(width: Int, height: Int, controller: ControllerModule.Controller)
    extends InitialPanel:
    self =>

    private val panelWidth = (width * 0.48).toInt
    private val panelHeight = (height * 0.65).toInt
    private val colorNotSelected = Color(238, 238, 238)
    private val colorSelected = Color(79, 195, 247)
    private var numLaps = 20
    private val carSelectionPanel = CarSelectionPanel(panelWidth, panelHeight, controller)
    private val initialRightPanel = ParamsSelectionPanel(panelWidth, panelHeight, controller)
    private val lapsLabel = createJLabel("Select laps:", Dimension((width * 0.06).toInt, (height * 0.06).toInt), SwingConstants.LEFT)
    private val rightArrowButton = createArrowButton("src/main/resources/arrows/arrow-right.png",  _ < 50, _ + 1)
    private val leftArrowButton = createArrowButton("src/main/resources/arrows/arrow-left.png",  _ > 20, _ - 1)
    private val lapsSelectedLabel = createJLabel(numLaps.toString, Dimension((width * 0.04).toInt, (height * 0.06).toInt), SwingConstants.CENTER)
    private val startBtn = createButton("Start Simulation")
    private val bottomPanel = createJPanel(width, height - panelHeight, FlowLayout())
    private val mainPanel = createMainPanelAndAddAllComponents()

    mainPanel foreach( p => self.add(p))

    def changeCar(carIndex: Int, tyresType: String): Unit = carSelectionPanel.changeCar(carIndex, tyresType)
    
    private def createJPanel(w: Int, h: Int, layout: LayoutManager): Task[JPanel] =
      for
        panel <- JPanel()
        _ <- panel.setPreferredSize(Dimension(w, h))
        _ <- panel.setLayout(layout)
      yield panel

    private def createButton(text: String): Task[JButton] =
      for
        button <- JButton(text)
        _ <- button.setPreferredSize(Dimension((width * 0.2).toInt, (height * 0.06).toInt))
      yield button

    private def createJLabel(text: String, dim: Dimension, pos: Int): Task[JLabel] =
      for
        label <- JLabel(text)
        _ <- label.setVerticalAlignment(SwingConstants.CENTER)
        _ <- label.setPreferredSize(dim)
        _ <- label.setHorizontalAlignment(pos)
      yield label

    private def createArrowButton(path: String, comparator: Int => Boolean, function: Int => Int): Task[JButton] =
      for
        button <- JButton(ImageIcon(path))
        _ <- button.setBorder(BorderFactory.createEmptyBorder())
        _ <- button.setBackground(colorNotSelected)
        _ <- button.addActionListener(e =>{
          if comparator(numLaps) then
            numLaps = function(numLaps)
            lapsSelectedLabel.foreach(e => e.setText(numLaps.toString))
        })
      yield button

    private def createMainPanelAndAddAllComponents(): Task[JPanel] =
      for
        mainp <- JPanel()
        _ <- mainp.setPreferredSize(Dimension(width, height))
        bottomPanel <- bottomPanel
        lapsSelectedLabel <- lapsSelectedLabel
        lapsLabel <- lapsLabel
        rightArrowButton <- rightArrowButton
        leftArrowButton <- leftArrowButton
        b <- startBtn
        paddingLabel <- JLabel()
        paddingLabel1 <- JLabel()
        _ <- paddingLabel.setPreferredSize(Dimension(width, (height * 0.03).toInt))
        _ <- paddingLabel1.setPreferredSize(Dimension(width, (height * 0.03).toInt))
        _ <- bottomPanel.add(paddingLabel)
        _ <- bottomPanel.add(lapsLabel)
        _ <- bottomPanel.add(leftArrowButton)
        _ <- bottomPanel.add(lapsSelectedLabel)
        _ <- bottomPanel.add(rightArrowButton)
        _ <- bottomPanel.add(paddingLabel1)
        _ <- bottomPanel.add(b)
        _ <- mainp.add(carSelectionPanel)
        _ <- mainp.add(initialRightPanel)
        _ <- mainp.add(bottomPanel)
      yield mainp
