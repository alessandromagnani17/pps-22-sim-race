package it.unibo.pps.view.main_panel

import it.unibo.pps.controller.ControllerModule
import it.unibo.pps.model.Tyre
import it.unibo.pps.utility.GivenConversion.GuiConversion.given
import it.unibo.pps.view.main_panel.{CarSelectionPanel, MainPanel, ParamsSelectionPanel, StartSimulationPanel}
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global

import java.awt.event.{ActionEvent, ActionListener, ItemEvent, ItemListener}
import java.awt.image.BufferedImage
import java.awt.*
import java.io.File
import javax.imageio.ImageIO
import javax.swing.*
import it.unibo.pps.view.ViewConstants.*

trait MainPanel extends JPanel:
  def updateDisplayedCar(): Unit
  def updateParametersPanel(): Unit


object MainPanel:
  def apply(width: Int, height: Int, controller: ControllerModule.Controller): MainPanel =
    MainPanelImpl(width, height, controller)

  private class MainPanelImpl(width: Int, height: Int, controller: ControllerModule.Controller) extends MainPanel:
    self =>

    private val carSelectionPanel = CarSelectionPanel(CAR_SELECT_PANEL_WIDTH, CAR_SELECT_PANEL_HEIGHT, controller)
    private val paramsSelectionPanel = ParamsSelectionPanel(CAR_SELECT_PANEL_WIDTH, CAR_SELECT_PANEL_HEIGHT, controller)
    private val startSimulationPanel = StartSimulationPanel(width, height - CAR_SELECT_PANEL_HEIGHT, controller)
    private val mainPanel = createMainPanelAndAddAllComponents()

    mainPanel foreach (p => self.add(p))

    def updateDisplayedCar(): Unit =
      carSelectionPanel.updateDisplayedCar()

    def updateParametersPanel(): Unit =
      paramsSelectionPanel.updateParametersPanel()

    private def createMainPanelAndAddAllComponents(): Task[JPanel] =
      for
        mainp <- JPanel()
        _ <- mainp.setPreferredSize(Dimension(width, height))
        _ <- mainp.add(carSelectionPanel)
        _ <- mainp.add(paramsSelectionPanel)
        _ <- mainp.add(startSimulationPanel)
      yield mainp
