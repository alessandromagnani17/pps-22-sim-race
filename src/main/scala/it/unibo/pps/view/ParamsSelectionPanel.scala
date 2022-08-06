package it.unibo.pps.view

import it.unibo.pps.controller.ControllerModule
import it.unibo.pps.utility.GivenConversion.GuiConversion.given
import monix.eval.Task

import java.awt.{BorderLayout, Color, Component, Dimension, FlowLayout, GridBagConstraints, GridBagLayout, LayoutManager}
import javax.swing.{BorderFactory, DefaultListCellRenderer, ImageIcon, JButton, JComboBox, JLabel, JList, JPanel, JSlider, SwingConstants}
import monix.execution.Scheduler.Implicits.global

import java.awt.event.{ActionEvent, ActionListener, ItemEvent, ItemListener}

trait ParamsSelectionPanel extends JPanel

object ParamsSelectionPanel:
  def apply(width: Int, height: Int, controller: ControllerModule.Controller): ParamsSelectionPanel = ParamsSelectionPanelImpl(width, height, controller)

  private class ParamsSelectionPanelImpl(width: Int, height: Int, controller: ControllerModule.Controller)
    extends ParamsSelectionPanel:
    self =>

    private val colorNotSelected = Color(238, 238, 238)
    private val colorSelected = Color(79, 195, 247)
    private var maxSpeed = 200
    private val tyresLabel = createLabel("Select tyres: ", Dimension(width, (height * 0.05).toInt), SwingConstants.CENTER, SwingConstants.BOTTOM)
    private val hardTyresButton = createButton("   Hard Tyres", "src/main/resources/tyres/hardtyres.png", "hard")
    private val mediumTyresButton = createButton("   Medium Tyres", "src/main/resources/tyres/mediumtyres.png", "medium")
    private val softTyresButton = createButton("   Soft Tyres", "src/main/resources/tyres/softtyres.png", "soft")
    private val tyresButtons = List(hardTyresButton, mediumTyresButton, softTyresButton)
    private val maxSpeedLabel = createLabel("Select Maximum Speed (km/h):", Dimension(width, (height * 0.1).toInt), SwingConstants.CENTER, SwingConstants.BOTTOM)
    private val speedSelectedLabel = createLabel(maxSpeed.toString, Dimension((width * 0.2).toInt, (height * 0.05).toInt), SwingConstants.CENTER, SwingConstants.CENTER)
    private val leftArrowButton = createArrowButton("src/main/resources/arrows/arrow-left.png", _ > 200, _-_)
    private val rightArrowButton = createArrowButton("src/main/resources/arrows/arrow-right.png", _ < 350, _+_)
    private val starAttackLabel = createLabel("Select Driver Attack Skills:", Dimension(width, (height * 0.1).toInt), SwingConstants.CENTER, SwingConstants.BOTTOM)
    private val starAttackButtons = createSkillsStarButtons("src/main/resources/stars/not-selected-star.png", "src/main/resources/stars/selected-star.png", true)
    private val starDefenseLabel = createLabel("Select Driver Defense Skills:", Dimension(width, (height * 0.1).toInt), SwingConstants.CENTER, SwingConstants.BOTTOM)
    private val starDefenseButtons = createSkillsStarButtons("src/main/resources/stars/not-selected-star.png", "src/main/resources/stars/selected-star.png", false)
    private val initialRightPanel = createPanelAndAddAllComponents()

    initialRightPanel foreach(e => self.add(e))

    private def createArrowButton(path: String, comparator: Int => Boolean, function: (Int, Int) => Int): Task[JButton] =
      for
        button <- JButton(ImageIcon(path))
        _ <- button.setBorder(BorderFactory.createEmptyBorder())
        _ <- button.setBackground(colorNotSelected)
        _ <- button.addActionListener(e => {
          if comparator(maxSpeed) then
            maxSpeed = function(maxSpeed, 10)
            speedSelectedLabel.foreach(e => e.setText(maxSpeed.toString))
        })
      yield button

    private def createButton(text: String, fileName: String, name: String): Task[JButton] =
      for
        button <- JButton(text, ImageIcon(fileName))
        _ <- button.setName(name)
        _ <- if name.equals("hard") then {button.setBackground(colorSelected); button.setOpaque(true)} else button.setBackground(colorNotSelected)
        _ <- button.setPreferredSize(Dimension((width * 0.3).toInt, (height * 0.09).toInt))
        _ <- button.addActionListener(e => {
          tyresButtons.foreach(e => e.foreach(f => {
            if f.getText == button.getText then
              f.setBackground(colorSelected)
              f.setOpaque(true)
              controller.changeDisplayedCar(f.getName)
            else
              f.setBackground(colorNotSelected)
          }))
        })
      yield button

    private def createSkillsStarButtons(filenameNotSelected: String, filenameSelected: String, isAttack: Boolean): List[Task[JButton]] =
      val buttons = for
        index <- 0 to 4
        button = createStarButton(filenameNotSelected, filenameSelected, index.toString, isAttack)
      yield button
      buttons.toList

    private def createStarButton(filenameNotSelected: String, filenameSelected: String, name: String, isAttack: Boolean): Task[JButton] =
      for
        button <- if name.equals("0") then JButton(ImageIcon(filenameSelected)) else JButton(ImageIcon(filenameNotSelected))
        _ <- button.setBorder(BorderFactory.createEmptyBorder())
        _ <- button.setPreferredSize(Dimension((width * 0.09).toInt, (height * 0.08).toInt))
        _ <- button.setName(name)
        _ <- button.setBackground(colorNotSelected)
        _ <- button.addActionListener(e =>{
          if isAttack then
            starAttackButtons.foreach(e => e.foreach(f =>
              if f.getName.toInt <= button.getName.toInt then
                f.setIcon(ImageIcon(filenameSelected))
              else
                f.setIcon(ImageIcon(filenameNotSelected))))
          else
            starDefenseButtons.foreach(e => e.foreach(f =>
              if f.getName.toInt <= button.getName.toInt then
                f.setIcon(ImageIcon(filenameSelected))
              else
                f.setIcon(ImageIcon(filenameNotSelected))))
        })
      yield button

    private def createLabel(text: String, dim: Dimension, horizontal: Int, vertical: Int): Task[JLabel] =
      for
        label <- JLabel(text)
        _ <- label.setPreferredSize(dim)
        _ <- label.setHorizontalAlignment(horizontal)
        _ <- label.setVerticalAlignment(vertical)
      yield label

    private def createPanelAndAddAllComponents(): Task[JPanel] =
      for
        panel <- JPanel()
        _ <- panel.setPreferredSize(Dimension(width, height))
        _ <- panel.setLayout(FlowLayout())
        _ <- panel.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, Color.BLACK))
        tyresLabel <- tyresLabel
        hardTyresButton <- hardTyresButton
        mediumTyresButton <- mediumTyresButton
        softTyresButton <- softTyresButton
        maxSpeedLabel <- maxSpeedLabel
        speedSelectedLabel <- speedSelectedLabel
        rightArrowButton <- rightArrowButton
        leftArrowButton <- leftArrowButton
        starAttackLabel <- starAttackLabel
        starDefenseLabel <- starDefenseLabel
        _ <- panel.add(tyresLabel)
        _ <- panel.add(hardTyresButton)
        _ <- panel.add(mediumTyresButton)
        _ <- panel.add(softTyresButton)
        _ <- panel.add(maxSpeedLabel)
        _ <- panel.add(leftArrowButton)
        _ <- panel.add(speedSelectedLabel)
        _ <- panel.add(rightArrowButton)
        _ <- panel.add(starAttackLabel)
        _ <- starAttackButtons.foreach(e => e.foreach(f => panel.add(f)))
        _ <- panel.add(starDefenseLabel)
        _ <- starDefenseButtons.foreach(e => e.foreach(f => panel.add(f)))
        _ <- panel.setVisible(true)
      yield panel