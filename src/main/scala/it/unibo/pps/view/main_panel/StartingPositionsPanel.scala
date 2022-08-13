package it.unibo.pps.view.main_panel

import it.unibo.pps.controller.ControllerModule
import it.unibo.pps.utility.GivenConversion.GuiConversion.given
import it.unibo.pps.view.StartingPositionsPanel
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global

import java.awt.{Color, Dimension, FlowLayout, GridBagConstraints, GridBagLayout}
import javax.swing.*

trait StartingPositionsPanel extends JPanel

object StartingPositionsPanel:
  def apply(width: Int, height: Int, controller: ControllerModule.Controller): StartingPositionsPanel =
    StartingPositionsPanelImpl(width, height, controller)

  private class StartingPositionsPanelImpl(width: Int, height: Int, controller: ControllerModule.Controller)
    extends StartingPositionsPanel:
    self =>

    // mettere costante
    private val numCars = 4
    private val carNames: Map[Int, String] = Map(0 -> "Ferrari", 1 -> "Mercedes", 2 -> "Red Bull", 3 -> "McLaren")
    private val labelList = createLabels()
    private val labelHeight = (height - 60) / numCars
    private val startingPositionsPanel = createPanelAndAddAllComponents()
    startingPositionsPanel foreach (e => self.add(e))

    private def createLabels(): List[Task[JLabel]] =
      val labels = for
        index <- 0 until numCars
        label = createLabel(index)
      yield label
      labels.toList

    private def createLabel(index: Int): Task[JLabel] =
      for
        label <- JLabel(carNames(index))
        _ <- label.setName(index.toString)
        _ <- label.setPreferredSize(Dimension(width, labelHeight))
        _ <- label.setBorder(BorderFactory.createMatteBorder(0,0,1,0, Color.BLACK))
      yield label

    private def createPanelAndAddAllComponents(): Task[JPanel] =
      for
        panel <- JPanel()
        _ <- panel.setPreferredSize(Dimension(width, height))
        _ <- panel.setLayout(FlowLayout())
        _ <- panel.setBackground(Color.RED)
        //_ <- addLabelsToPanel(labelList, panel)
        _ <- panel.setVisible(true)
      yield panel

    private def addLabelsToPanel(labels: List[Task[JLabel]], panel: JPanel): Task[Unit] =
      for task <- labels do addLabel(task, panel)

    private def addLabel(task: Task[JLabel], panel: JPanel): Task[Unit] =
      val p = for
        label <- task
        _ <- panel.add(label)
      yield ()
      p.runSyncUnsafe()
