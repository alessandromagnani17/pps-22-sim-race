package it.unibo.pps.view

import it.unibo.pps.controller.ControllerModule
import it.unibo.pps.utility.GivenConversion.GuiConversion.given
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import java.io.File

import java.awt.event.{ItemEvent, ItemListener}
import java.awt.image.BufferedImage
import java.awt.{BorderLayout, Color, Component, Dimension, FlowLayout, GridBagConstraints, GridBagLayout, LayoutManager}
import javax.imageio.ImageIO
import javax.swing.{BorderFactory, DefaultListCellRenderer, ImageIcon, JButton, JComboBox, JLabel, JList, JPanel}

trait InitialPanel extends JPanel

object InitialPanel:
  def apply(width: Int, height: Int, controller: ControllerModule.Controller): InitialPanel = InitialPanelImpl(width, height, controller)

  private class InitialPanelImpl(width: Int, height: Int, controller: ControllerModule.Controller)
    extends InitialPanel:
    self =>

    private val carLabelCombo = createJLabel("Select a car: ")
    private val carComboBox = createJComboBox(List("1", "2", "3", "4"))
    //private val img = createBufferedImg("/Logo_UNI.png ")
    private val carPanel = createJPanel(width/2, height - 100, FlowLayout())
    private val paramPanel = createJPanel(width/2, height - 100, FlowLayout())
    private val startBtn = createButton("Start Simulation")
    private val bottomPanel = createJPanel(350, 40, BorderLayout())
    private val mainPanel = createMainPanelAndAddAllComponents()

    mainPanel foreach( p => self.add(p))

    /*private def addAllComponents(): Task[Unit] =
      val addComponent = for
        mp <- mainPanel
        c <- carPanel
        pp <- paramPanel
        bp <- bottomPanel
        _ <- c.add(JButton("Provetta"))
      yield ()
      addComponent.runAsyncAndForget*/

    private def createBufferedImg(path: String): Task[JLabel] =
      for
        bi <- ImageIO.read(File(path))
        jl <- JLabel(ImageIcon(bi))
      yield jl

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
            if e.getStateChange == ItemEvent.SELECTED then println("Car selected: " + e.getItem)
        })
        _ <- cb.setOpaque(true)
      yield cb

    private def createJPanel(w: Int, h: Int, layout: LayoutManager): Task[JPanel] =
      for
        panel <- JPanel()
        _ <- panel.setPreferredSize(Dimension(w, h))
        _ <- panel.setBorder(BorderFactory.createLineBorder(Color.BLACK))
        _ <- panel.setLayout(layout)
      yield panel

    private def createButton(title: String): Task[JButton] =
      for
        btn <- JButton(title)
      yield btn

    private def createMainPanelAndAddAllComponents(): Task[JPanel] =
      for
        mainp <- JPanel()
        _ <- mainp.setPreferredSize(Dimension(width, height))
        _ <- mainp.setLayout(GridBagLayout())
        cp <- carPanel
        pp <- paramPanel
        bp <- bottomPanel

        b <- startBtn
        _ <- bp.add(b, BorderLayout.EAST)

        jl <- carLabelCombo
        combo <- carComboBox
        //img <- img
        _ <- cp.add(jl)
        _ <- cp.add(combo)
        //_ <- cp.add(img)


        // Adding car panel, param panel and bottom panel
        gbc <- GridBagConstraints()
        _ <- gbc.gridx = 0
        _ <- gbc.gridy = 0
        _ <- mainp.add(cp, gbc)
        _ <- gbc.gridx = 1
        _ <- gbc.gridy = 0
        _ <- mainp.add(pp, gbc)
        //_ <- gbc.gridx = 0
        _ <- gbc.gridy = 1
        _ <- mainp.add(bp, gbc)
      yield mainp
