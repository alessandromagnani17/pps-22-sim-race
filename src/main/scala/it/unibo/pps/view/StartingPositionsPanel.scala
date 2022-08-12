package it.unibo.pps.view

import it.unibo.pps.controller.ControllerModule
import monix.eval.Task

import java.awt.{Dimension, FlowLayout}
import javax.swing.{JLabel, JPanel}
import it.unibo.pps.utility.GivenConversion.GuiConversion.given
import monix.execution.Scheduler.Implicits.global

trait StartingPositionsPanel extends JPanel

object StartingPositionsPanel:
  def apply(width: Int, height: Int, controller: ControllerModule.Controller): StartingPositionsPanel =
    StartingPositionsPanelImpl(width, height, controller)

  private class StartingPositionsPanelImpl(width: Int, height: Int, controller: ControllerModule.Controller)
    extends StartingPositionsPanel:
    self =>
    private val startingPositionsPanel = createPanelAndAddAllComponents()
    startingPositionsPanel foreach (e => self.add(e))

    private def createPanelAndAddAllComponents(): Task[JPanel] =
      for
        panel <- JPanel()
        _ <- println("creo")
        _ <- panel.setPreferredSize(Dimension(width, height))
        _ <- panel.setLayout(FlowLayout())
        _ <- panel.setVisible(true)
      yield panel
