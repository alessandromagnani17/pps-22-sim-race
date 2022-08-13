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
    private val carNames: Map[Int, String] = Map(0 -> "Ferrari", 1 -> "Mercedes", 2 -> "Red Bull", 3 -> "McLaren")
    private val imageLoader = ImageLoader()
    private val topLabelHeight = (height * 0.15).toInt
    private val numCars = 4
    private val labelHeight = ((height * 0.75).toInt - topLabelHeight) / numCars
    private val colorNotSelected = Color(238, 238, 238)
    private val colorSelected = Color(79, 195, 247)
    private val topLabel = createLabel("Imposta la griglia di partenza", Dimension(width, topLabelHeight), SwingConstants.CENTER, false)
    private val positionPanel = createPanel(Dimension(width, height - (height * 0.22).toInt))
    private val positions = createMap()
    private val startingPositionsPanel = createPanelAndAddAllComponents()

    startingPositionsPanel foreach (e => self.add(e))

    private def createMap(): scala.collection.mutable.Map[Int, (Task[JLabel], Task[JLabel], Task[JButton], Task[JButton])] =
      val map: Map[Int, (Task[JLabel], Task[JLabel], Task[JButton], Task[JButton])] = scala.collection.mutable.Map.empty
      for i <- 0 until numCars do
        map += (i -> (createLabel(s"/cars/miniatures/$i.png", Dimension((width * 0.3).toInt, (height * 0.15).toInt), SwingConstants.CENTER, true),
          createLabel(s"${i + 1}: ${carNames(i)}", Dimension((width * 0.15).toInt, labelHeight), SwingConstants.LEFT, false),
          createButton(i, "/arrows/arrow-up.png", e => if e == 0 then e else e - 1),
          createButton(i, "/arrows/arrow-bottom.png", e => if e == (numCars - 1) then e else e + 1)))
      map

    private def createLabel(text: String, dim: Dimension, horizontal: Int, isImage: Boolean): Task[JLabel] =
      for
        label <- if isImage then JLabel(imageLoader.load(text)) else JLabel(text)
        _ <- label.setPreferredSize(dim)
        _ <- label.setHorizontalAlignment(horizontal)
      yield label

    private def createButton(index: Int, path: String, calcIndex: Int => Int): Task[JButton] =
      for
        button <- JButton(imageLoader.load(path))
        _ <- button.setBorder(BorderFactory.createEmptyBorder())
        _ <- button.setBackground(colorNotSelected)
        _ <- button.setHorizontalAlignment(SwingConstants.RIGHT)
        _ <- button.addActionListener { e =>

          val nextIndex = calcIndex(index)

          controller.invertPosition(index, nextIndex)


          // Controller --> 1 -> Ferrari | 2 -> Mercedes
          // 1 - Ferrari
          // 2 - Mercedes

          // Invertiamo premendo il basso di Ferrari

          // Controller diventa --> 1 -> Mercedes | 2 -> Ferrari

          // index è 0 mentre nextIndex è 1


          // 1 - Mercedes
          // 2 - Ferrari
          
          println("index -> " + index + "| next index -> " + nextIndex)

          positions.get(index).get(1).foreach(e => e.setText(s"${index + 1}: ${carNames(nextIndex)}"))
          positions.get(nextIndex).get(1).foreach(e => e.setText(s"${nextIndex + 1}: ${carNames(index)}"))










          //positions.get(nextIndex).get(0).foreach(e => e.setIcon(imageLoader.load(s"/cars/miniatures/${controller.getStartingPositions().find(_._2)}.png")))
          //positions.get(index).get(0).foreach(e => e.setIcon(imageLoader.load(s"/cars/miniatures/$nextIndex.png")))


          //s"${i + 1}: ${carNames(i)}"

          //positions.get(nextIndex).get(1).foreach(e => e.setText("prova"))



          // sotto..

          /*val nextIndex = calcIndex(controller.getCurrentCarIndex())
          controller.setCurrentCarIndex(nextIndex.toInt)
          controller.getCurrentCar().path = s"/cars/$nextIndex-${controller.getCurrentCar().tyre.toString.toLowerCase}.png"
          updateDisplayedCar()
          controller.updateParametersPanel()
          carSelectedLabel.foreach(e => e.setText(s"Car selected: ${carNames(controller.getCurrentCarIndex())}"))*/
          println("Premuto")
        }
      yield button

    private def createPanel(dim: Dimension): Task[JPanel] =
      for
        panel <- JPanel()
        _ <- panel.setPreferredSize(dim)
      yield panel

    private def createPanelAndAddAllComponents(): Task[JPanel] =
      for
        panel <- JPanel()
        _ <- panel.setPreferredSize(Dimension(width, height))
        topLabel <- topLabel
        positionPanel <- positionPanel
        _ <- positions.foreach(e => addToPanel(e._2, positionPanel))
        _ <- panel.add(topLabel)
        _ <- panel.add(positionPanel)
        _ <- panel.setVisible(true)
      yield panel

    private def addToPanel(elem: (Task[JLabel], Task[JLabel], Task[JButton], Task[JButton]), posPanel: JPanel): Task[Unit] =
      val p = for
        panel <- JPanel()
        _ <- panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.BLACK))
        img <- elem._1
        label <- elem._2
        b1 <- elem._3
        b2 <- elem._4
        _ <- panel.add(label)
        _ <- panel.add(img)
        _ <- panel.add(b1)
        _ <- panel.add(b2)
        _ <- posPanel.add(panel)
      yield ()
      p.runSyncUnsafe()