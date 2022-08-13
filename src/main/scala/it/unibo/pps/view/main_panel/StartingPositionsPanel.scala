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
    private val topLabel = createLabel("Imposta la griglia di partenza")
    private val positionPanel = createPanel(Dimension(width, height - (height * 0.22).toInt))
    private val positions = prova()

    //private val labelList = createLabels()
    private val labelHeight = (height - (height * 0.22).toInt) / numCars
    println("LAbel --> " + labelHeight)
    private val startingPositionsPanel = createPanelAndAddAllComponents()
    startingPositionsPanel foreach (e => self.add(e))

    private def prova(): scala.collection.mutable.Map[Int, (Task[JPanel], Task[JLabel], Task[JLabel], Task[JButton], Task[JButton])] =
      val map: Map[Int, (Task[JPanel], Task[JLabel], Task[JLabel], Task[JButton], Task[JButton])] = scala.collection.mutable.Map.empty
      for i <- 0 until numCars do
        map += (i -> (createPanel2(Dimension(width, labelHeight)), createImageLabel(i), createLabel(s"${i + 1}: ${carNames(i)}"), createButton("/arrows/arrow-up.png"), createButton("/arrows/arrow-bottom.png")))
      map

    private def createImageLabel(index: Int): Task[JLabel] =
      for
        label <- JLabel(imageLoader.load(s"/cars/miniatures/$index.png"))
      yield label

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
      yield panel

    private def createPanel2(dim: Dimension): Task[JPanel] =
      for
        panel <- JPanel()
        _ <- panel.setLayout(FlowLayout())
        _ <- panel.setSize(dim)
        _ <- panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.BLACK))
        //_ <- panel.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 5))
      yield panel

    private def createLabel(text: String): Task[JLabel] =
      for
        label <- JLabel(text)
        //_ <- label.setBorder(BorderFactory.createLineBorder(Color.GREEN, 6))
        _ <- label.setHorizontalAlignment(SwingConstants.CENTER)
      yield label

    private def createPanelAndAddAllComponents(): Task[JPanel] =
      for
        panel <- JPanel()
        _ <- println(s"Creato panel di width: $width e height: $height")
        _ <- panel.setPreferredSize(Dimension(width, height))
        //_ <- panel.setLayout(null)
        //_ <- panel.setBackground(Color.RED)
        //_ <- addLabelsToPanel(labelList, panel)
        topLabel <- topLabel
        _ <- topLabel.setPreferredSize(Dimension(width, (height * 0.15).toInt))
        positionPanel <- positionPanel

        _ <- positions.foreach(e => addToPanel(e._2, positionPanel))


        _ <- panel.add(topLabel)
        _ <- panel.add(positionPanel)
        _ <- panel.setVisible(true)
      yield panel

    private def addToPanel(elem: (Task[JPanel], Task[JLabel], Task[JLabel], Task[JButton], Task[JButton]), posPanel: JPanel): Task[Unit] =
      val p = for
        panel <- elem._1
        img <- elem._2
        label <- elem._3
        b1 <- elem._4
        b2 <- elem._5
        _ <- label.setPreferredSize(Dimension((width * 0.15).toInt, (height * 0.15).toInt))
        _ <- img.setPreferredSize(Dimension((width * 0.3).toInt, (height * 0.15).toInt))
        _ <- img.setHorizontalAlignment(SwingConstants.CENTER)
        _ <- label.setHorizontalAlignment(SwingConstants.LEFT)
        _ <- b1.setHorizontalAlignment(SwingConstants.RIGHT)
        _ <- b2.setHorizontalAlignment(SwingConstants.RIGHT)
        _ <- panel.add(label)
        _ <- panel.add(img)
        _ <- panel.add(b1)
        _ <- panel.add(b2)
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
