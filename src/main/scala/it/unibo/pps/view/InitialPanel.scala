package it.unibo.pps.view

import it.unibo.pps.controller.ControllerModule
import it.unibo.pps.utility.GivenConversion.GuiConversion.given
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global

import java.awt.{BorderLayout, Color, Dimension, FlowLayout, GridBagConstraints, GridBagLayout}
import javax.swing.{BorderFactory, JButton, JPanel}

trait InitialPanel extends JPanel

object InitialPanel:
  def apply(width: Int, height: Int, controller: ControllerModule.Controller): InitialPanel = InitialPanelImpl(width, height, controller)

  private class InitialPanelImpl(width: Int, height: Int, controller: ControllerModule.Controller)
    extends InitialPanel:
    self =>

    private val carPanel = createJPanel(width/2, height - 100)
    private val paramPanel = createJPanel(width/2, height - 100)
    private val startBtn = createButton("Start Simulation")
    private val bottomPanel = createBottomPanel(350, 40)
    private val mainPanel = createMainPanelAndAddAllComponents()

    //addAllComponents()

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

    private def createJPanel(w: Int, h: Int): Task[JPanel] =
      for
        panel <- JPanel()
        _ <- panel.setPreferredSize(Dimension(w, h))
        _ <- panel.setBorder(BorderFactory.createLineBorder(Color.BLACK))
      yield panel

    private def createBottomPanel(w: Int, h: Int): Task[JPanel] =
      for
        panel <- JPanel()
        _ <- panel.setPreferredSize(Dimension(w, h))
        _ <- panel.setLayout(new BorderLayout())
        b <- startBtn
        _ <- panel.add(b, BorderLayout.EAST)
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
        _ <- cp.add(JButton("Provetta"))
        pp <- paramPanel
        bp <- bottomPanel
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
