package it.unibo.pps.view.main_panel

import it.unibo.pps.controller.ControllerModule
import it.unibo.pps.utility.GivenConversion.GuiConversion.given
import it.unibo.pps.view.main_panel.StartingPositionsPanel
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import scala.collection.mutable.Map

import java.awt.{BorderLayout, Color, Dimension, FlowLayout, GridBagConstraints, GridBagLayout}
import javax.swing.*

trait StartingPositionsPanel extends JPanel

object StartingPositionsPanel:
  def apply(width: Int, height: Int, controller: ControllerModule.Controller): StartingPositionsPanel =
    StartingPositionsPanelImpl(width, height, controller)

  private class StartingPositionsPanelImpl(width: Int, height: Int, controller: ControllerModule.Controller)
    extends StartingPositionsPanel:
    self =>

    // mettere costante
    private val imageLoader = ImageLoader()
    private val numCars = 4
    private val colorNotSelected = Color(238, 238, 238)
    private val colorSelected = Color(79, 195, 247)
    private val carNames: Map[Int, String] = Map(0 -> "Ferrari", 1 -> "Mercedes", 2 -> "Red Bull", 3 -> "McLaren")
    private val topLabel = createLabel("Imposta la griglia di partenza", Dimension(width, (height * 0.1).toInt))
    private val positionPanel = createPanel(Dimension(width, height - (height * 0.23).toInt))
    private val positions = prova()

    //private val labelList = createLabels()
    private val labelHeight = (height - (height * 0.23).toInt) / numCars
    private val startingPositionsPanel = createPanelAndAddAllComponents()
    startingPositionsPanel foreach (e => self.add(e))

    private def prova(): scala.collection.mutable.Map[Int, (Task[JPanel], Task[JLabel], Task[JButton], Task[JButton])] =
      val map: Map[Int, (Task[JPanel], Task[JLabel], Task[JButton], Task[JButton])] = scala.collection.mutable.Map.empty
      for i <- 0 until numCars do
        map += (i -> (createPanel(Dimension(width, labelHeight)), createLabel(carNames(i), Dimension(width, labelHeight)), createButton("/arrows/arrow-up.png"), createButton("/arrows/arrow-bottom.png")))
      map

    private def createButton(path: String): Task[JButton] =
      for
        button <- JButton(imageLoader.load(path))
        _ <- button.setBorder(BorderFactory.createEmptyBorder())
        _ <- button.setBackground(colorNotSelected)
      yield button

    private def createPanel(dim: Dimension): Task[JPanel] =
      for
        panel <- JPanel()
        _ <- panel.setPreferredSize(dim)
        _ <- panel.setBackground(Color.YELLOW)
        //_ <- panel.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 5))
      yield panel

    private def createLabel(text: String, dim: Dimension): Task[JLabel] =
      for
        label <- JLabel(text)
        _ <- label.setBorder(BorderFactory.createLineBorder(Color.GREEN, 6))
        _ <- label.setPreferredSize(dim)
        _ <- label.setHorizontalAlignment(SwingConstants.CENTER)
      yield label

    /*private def createLabels(): List[Task[JLabel]] =
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
        _ <- label.setHorizontalAlignment(SwingConstants.CENTER)
        _ <- label.setBorder(BorderFactory.createMatteBorder(0,0,1,0, Color.BLACK))
      yield label*/

    private def createPanelAndAddAllComponents(): Task[JPanel] =
      for
        panel <- JPanel()
        _ <- println(s"Creato panel di width: $width e height: $height")
        _ <- panel.setPreferredSize(Dimension(width, height))
        //_ <- panel.setLayout(null)
        _ <- panel.setBackground(Color.RED)
        //_ <- addLabelsToPanel(labelList, panel)
        topLabel <- topLabel
        positionPanel <- positionPanel

        // Aggiungo solo la prima label di prova
        lab <- positions(0)._2
        _ <- println("Label --> " + lab.getText)

        xx <- JLabel("hdugfgf")
        _ <- positionPanel.add(lab)

        _ <- panel.add(topLabel)
        _ <- panel.add(positionPanel)
        /*_ <- positions.foreach(e => addToPanel(e._2, positionPanel))
        _ <- panel.add(topLabel)
        _ <- panel.add(positionPanel)*/
        _ <- panel.setVisible(true)
      yield panel

    private def addToPanel(elem: (Task[JPanel], Task[JLabel], Task[JButton], Task[JButton]), posPanel: JPanel): Task[Unit] =
      val p = for
        panel <- elem._1
        label <- elem._2
        /*b1 <- elem._3
        b2 <- elem._4
        _ <- panel.add(label)
        _ <- panel.add(b1)
        _ <- panel.add(b2)*/
        l <- JLabel("provetta")
        _ <- panel.add(l)
        _ <- posPanel.add(panel)
      yield ()
      p.runSyncUnsafe()


    private def addLabelsToPanel(labels: List[Task[JLabel]], panel: JPanel): Task[Unit] =
      for task <- labels do addLabel(task, panel)

    private def addLabel(task: Task[JLabel], panel: JPanel): Task[Unit] =
      val p = for
        label <- task
        _ <- panel.add(label)
      yield ()
      p.runSyncUnsafe()
