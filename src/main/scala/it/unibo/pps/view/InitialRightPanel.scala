package it.unibo.pps.view

import it.unibo.pps.controller.ControllerModule
import it.unibo.pps.utility.GivenConversion.GuiConversion.given
import monix.eval.Task

import java.awt.{BorderLayout, Color, Component, Dimension, FlowLayout, GridBagConstraints, GridBagLayout, LayoutManager}
import javax.swing.{BorderFactory, DefaultListCellRenderer, ImageIcon, JButton, JComboBox, JLabel, JList, JPanel, JSlider, SwingConstants}
import monix.execution.Scheduler.Implicits.global

import java.awt.event.{ActionEvent, ActionListener, ItemEvent, ItemListener}

trait InitialRightPanel extends JPanel

object InitialRightPanel:
  def apply(width: Int, height: Int, controller: ControllerModule.Controller): InitialRightPanel = InitialRightPanelImpl(width, height, controller)

  private class InitialRightPanelImpl (width: Int, height: Int, controller: ControllerModule.Controller)
    extends InitialRightPanel:
    self =>

    private val initialRightPanel = createPanel()

    private val tyresLabel = createJLabel("Select tyres: ")
    private val hardTyresButton = createJButton("   Hard Tyres", "src/main/resources/tyres/hardtyres.png", "hard")
    private val mediumTyresButton = createJButton("   Medium Tyres", "src/main/resources/tyres/mediumtyres.png", "medium")
    private val softTyresButton = createJButton("   Soft Tyres", "src/main/resources/tyres/softtyres.png", "soft")

    private val tyresButtons = List(hardTyresButton, mediumTyresButton, softTyresButton)



    private val maxSpeedLabel = createJLabel("Select Maximum Speed (km/h):")
    private var maxSpeed = 200

    private val rightArrowButton = createRightArrowButton("src/main/resources/arrows/arrow-right.png")
    private val leftArrowButton = createLeftArrowButton("src/main/resources/arrows/arrow-left.png")
    private val speedSelectedLabel = createJLabel(maxSpeed.toString)

    //private val lapsSelectedLabel = createJLabel(numLaps.toString)
    private val starAttackLabel = createJLabel("Select Driver Attack Skills:")
    private val starAttackButtons = createSkillsStarButtons("src/main/resources/stars/not-selected-star.png", "src/main/resources/stars/selected-star.png", true)

    private val starDefenseLabel = createJLabel("Select Driver Defense Skills:")
    private val starDefenseButtons = createSkillsStarButtons("src/main/resources/stars/not-selected-star.png", "src/main/resources/stars/selected-star.png", false)


    private val colorNotSelected = Color(238, 238, 238)
    private val colorSelected = Color(79, 195, 247)

    initialRightPanel foreach(e => self.add(e))




    private def createJButton(text: String, fileName: String, name: String): Task[JButton] =
      for
        button <- JButton(text, ImageIcon(fileName))
        _ <- button.setName(name)
        _ <- button.setBackground(colorNotSelected)
        _ <- button.setPreferredSize(Dimension((width * 0.3).toInt, (height * 0.07).toInt))
        _ <- button.addActionListener(new ActionListener {
          override def actionPerformed(e: ActionEvent): Unit = tyresButtons.foreach(e => e.foreach(f => {
            if f.getText == button.getText then
              f.setBackground(colorSelected)
              f.setOpaque(true)
              controller.changeDisplayedCar(f.getName)
              
            else
              f.setBackground(colorNotSelected)
          }))
        })
      yield button


    private def createRightArrowButton(filename: String): Task[JButton] =
      for
        button <- JButton(ImageIcon(filename))
        _ <- button.setBorder(BorderFactory.createEmptyBorder())
        _ <- button.setBackground(colorNotSelected)
        _ <- button.addActionListener(e =>{
          if maxSpeed < 350 then
            maxSpeed = maxSpeed + 10
            speedSelectedLabel.foreach(e => e.setText(maxSpeed.toString))
        })
      yield button

    private def createLeftArrowButton(filename: String): Task[JButton] =
      for
        button <- JButton(ImageIcon(filename))
        _ <- button.setBorder(BorderFactory.createEmptyBorder())
        _ <- button.setBackground(colorNotSelected)
        _ <- button.addActionListener(e =>{
          if maxSpeed > 200 then
            maxSpeed = maxSpeed - 10
            speedSelectedLabel.foreach(e => e.setText(maxSpeed.toString))
        })
      yield button


    private def createSkillsStarButtons(filenameNotSelected: String, filenameSelected: String, isAttack: Boolean): List[Task[JButton]] =
      val starButtons: List[Task[JButton]] = List(createStarButton(filenameNotSelected, filenameSelected, 0.toString, isAttack),
        createStarButton(filenameNotSelected, filenameSelected, 1.toString, isAttack),
        createStarButton(filenameNotSelected, filenameSelected, 2.toString, isAttack),
        createStarButton(filenameNotSelected, filenameSelected, 3.toString, isAttack),
        createStarButton(filenameNotSelected, filenameSelected, 4.toString, isAttack))

      starButtons


    private def createStarButton (filenameNotSelected: String, filenameSelected: String, name: String, isAttack: Boolean): Task[JButton] =
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



    private def createJLabel(text: String): Task[JLabel] =
      for
        label <- JLabel(text)
      yield label

    private def createPanel(): Task[JPanel] =
      for
        panel <- JPanel()
        _ <- panel.setPreferredSize(Dimension(width, height))
        _ <- panel.setLayout(FlowLayout())
        _ <- panel.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, Color.BLACK))

        tyresLabel <- tyresLabel
        _ <- tyresLabel.setPreferredSize(Dimension(width, (height * 0.05).toInt))
        _ <- tyresLabel.setHorizontalAlignment(SwingConstants.CENTER)
        _ <- tyresLabel.setVerticalAlignment(SwingConstants.BOTTOM)


        maxSpeedLabel <- maxSpeedLabel
        _ <- maxSpeedLabel.setPreferredSize(Dimension(width, (height * 0.1).toInt))
        _ <- maxSpeedLabel.setHorizontalAlignment(SwingConstants.CENTER)
        _ <- maxSpeedLabel.setVerticalAlignment(SwingConstants.BOTTOM)

        speedSelectedLabel <- speedSelectedLabel
        _ <- speedSelectedLabel.setPreferredSize(Dimension((width * 0.2).toInt, (height * 0.05).toInt))
        _ <- speedSelectedLabel.setHorizontalAlignment(SwingConstants.CENTER)
        _ <- speedSelectedLabel.setVerticalAlignment(SwingConstants.CENTER)
        rab <- rightArrowButton
        lab <- leftArrowButton

        hardTyresButton <- hardTyresButton
        mediumTyresButton <- mediumTyresButton
        softTyresButton <- softTyresButton

        starAttackLabel <- starAttackLabel
        _ <- starAttackLabel.setPreferredSize(Dimension(width, (height * 0.1).toInt))
        _ <- starAttackLabel.setHorizontalAlignment(SwingConstants.CENTER)
        _ <- starAttackLabel.setVerticalAlignment(SwingConstants.BOTTOM)

        starDefenseLabel <- starDefenseLabel
        _ <- starDefenseLabel.setPreferredSize(Dimension(width, (height * 0.1).toInt))
        _ <- starDefenseLabel.setHorizontalAlignment(SwingConstants.CENTER)
        _ <- starDefenseLabel.setVerticalAlignment(SwingConstants.BOTTOM)


        _ <- hardTyresButton.setBackground(colorSelected)
        _ <- hardTyresButton.setOpaque(true)

        _ <- panel.add(tyresLabel)
        _ <- panel.add(hardTyresButton)
        _ <- panel.add(mediumTyresButton)
        _ <- panel.add(softTyresButton)
        _ <- panel.add(maxSpeedLabel)
        _ <- panel.add(lab)
        _ <- panel.add(speedSelectedLabel)
        _ <- panel.add(rab)
        _ <- panel.add(starAttackLabel)
        _ <- starAttackButtons.foreach(e => e.foreach(f => panel.add(f)))
        _ <- panel.add(starDefenseLabel)
        _ <- starDefenseButtons.foreach(e => e.foreach(f => panel.add(f)))



        //_ <- panel.add(maximumSpeed)
        _ <- panel.setVisible(true)
      yield panel