package it.unibo.pps.view.main_panel

import it.unibo.pps.controller.ControllerModule
import it.unibo.pps.model.Tyre
import it.unibo.pps.utility.GivenConversion.GuiConversion.given
import it.unibo.pps.view.main_panel.{CarSelectionPanel, MainPanel, ParamsSelectionPanel, StartSimulationPanel}
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import it.unibo.pps.utility.PimpScala.RichJPanel.*


import java.awt.event.{ActionEvent, ActionListener, ItemEvent, ItemListener}
import java.awt.image.BufferedImage
import java.awt.*
import java.io.File
import javax.imageio.ImageIO
import javax.swing.*
import it.unibo.pps.view.Constants.MainPanelConstants.*

trait MainPanel extends JPanel:

  /**  Method that updates the car displayed */
  def updateDisplayedCar: Unit

  /**  Method that updates the displayed parameters when the car displayed is changed */
  def updateParametersPanel: Unit


object MainPanel:
  def apply(controller: ControllerModule.Controller): MainPanel =
    MainPanelImpl(controller)

  private class MainPanelImpl(controller: ControllerModule.Controller) extends MainPanel:
    self =>

    private val carSelectionPanel = CarSelectionPanel(controller)
    private val paramsSelectionPanel = ParamsSelectionPanel(controller)
    private val startSimulationPanel = StartSimulationPanel(controller)
    private val mainPanel = createMainPanelAndAddAllComponents()

    mainPanel foreach (p => self.add(p))

    def updateDisplayedCar: Unit =
      carSelectionPanel.updateDisplayedCar()

    def updateParametersPanel: Unit =
      paramsSelectionPanel.updateParametersPanel()

    private def createMainPanelAndAddAllComponents(): Task[JPanel] =
      for
        mainp <- JPanel()
        _ <- mainp.setPreferredSize(Dimension(FRAME_WIDTH, FRAME_HEIGHT))
        _ <- mainp.add(carSelectionPanel)
        _ <- mainp.add(paramsSelectionPanel)
        _ <- mainp.add(startSimulationPanel)
      yield mainp
