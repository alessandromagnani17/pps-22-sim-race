package it.unibo.pps.view

import it.unibo.pps.controller.ControllerModule
import javax.swing.JPanel

trait InitialPanel extends JPanel

object InitialPanel:
  def apply(width: Int, height: Int, controller: ControllerModule.Controller): InitialPanel = InitialPanelImpl(width, height, controller)

  private class InitialPanelImpl(width: Int, height: Int, controller: ControllerModule.Controller)
    extends InitialPanel:




