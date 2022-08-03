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

trait InitialPanel extends JPanel

object InitialPanel:
  def apply(width: Int, height: Int, controller: ControllerModule.Controller): InitialPanel = InitialPanelImpl(width, height, controller)

  private class InitialPanelImpl(width: Int, height: Int, controller: ControllerModule.Controller)
    extends InitialPanel:
    self =>

    private val panelWidth = (width * 0.5).toInt
    private val panelHeight = (height * 0.9).toInt

    // private val carLabelCombo = createJLabel("Select a car: ")
    // private val carComboBox = createJComboBox(List("1", "2", "3", "4"))
    // private val carPanel = createJPanel(panelWidth, panelHeight, FlowLayout())
    private val initialLeftPanel = InitialLeftPanel(panelWidth, panelHeight, controller)
    private val initialRightPanel = InitialRightPanel(panelWidth, panelHeight, controller)

    // private val paramPanel = createJPanel(panelWidth, panelHeight, FlowLayout())
    private val startBtn = createButton("Start Simulation")
    private val bottomPanel = createJPanel(panelWidth, height - panelHeight, BorderLayout())
    private val mainPanel = createMainPanelAndAddAllComponents()

    mainPanel foreach( p => self.add(p))



    private def createJPanel(w: Int, h: Int, layout: LayoutManager): Task[JPanel] =
      for
        panel <- JPanel()
        _ <- panel.setPreferredSize(Dimension(w, h))
        _ <- panel.setBorder(BorderFactory.createLineBorder(Color.BLACK))
        _ <- panel.setLayout(layout)
      yield panel

    private def createButton(text: String): Task[JButton] =
      for
        btn <- JButton(text)
      yield btn

    private def createMainPanelAndAddAllComponents(): Task[JPanel] =
      for
        mainp <- JPanel()
        _ <- mainp.setPreferredSize(Dimension(width, height))
        _ <- mainp.setLayout(BorderLayout())
        bp <- bottomPanel
        _ <- bp.setBackground(Color.RED)

        b <- startBtn

        _ <- mainp.add(initialLeftPanel, BorderLayout.WEST)
        _ <- mainp.add(initialRightPanel, BorderLayout.EAST)
        _ <- mainp.add(bp, BorderLayout.SOUTH)

      yield mainp
