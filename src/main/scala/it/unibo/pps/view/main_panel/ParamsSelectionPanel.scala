package it.unibo.pps.view.main_panel

import it.unibo.pps.controller.ControllerModule
import it.unibo.pps.model.Tyre
import it.unibo.pps.utility.GivenConversion.GuiConversion.given
import it.unibo.pps.utility.Matcher
import it.unibo.pps.view.main_panel.{ImageLoader, ParamsSelectionPanel}
import monix.eval.{Task, TaskLift}
import monix.execution.Scheduler.Implicits.global

import java.awt.event.{ActionEvent, ActionListener, ItemEvent, ItemListener}
import java.awt.{BorderLayout, Color, Component, Dimension, FlowLayout, GridBagConstraints, GridBagLayout, LayoutManager}
import javax.swing.*

trait ParamsSelectionPanel extends JPanel:
  def updateParametersPanel(): Unit

object ParamsSelectionPanel:
  def apply(width: Int, height: Int, controller: ControllerModule.Controller): ParamsSelectionPanel =
    ParamsSelectionPanelImpl(width, height, controller)

  private class ParamsSelectionPanelImpl(width: Int, height: Int, controller: ControllerModule.Controller)
    extends ParamsSelectionPanel:
    self =>
    private val imageLoader = ImageLoader()
    private val matcher = Matcher()
    private val colorNotSelected = Color(238, 238, 238)
    private val colorSelected = Color(79, 195, 247)
    private val fileNameSelected = "/stars/selected-star.png"
    private val fileNameNotSelected = "/stars/not-selected-star.png"
    private val minSpeed = 200
    private val maxSpeed = 350
    private val tyresLabel = createLabel(
      "Select tyres: ",
      Dimension(width, (height * 0.05).toInt),
      SwingConstants.CENTER,
      SwingConstants.BOTTOM
    )
    private val hardTyresButton = createButton("   Hard Tyres", "/tyres/hardtyres.png", Tyre.HARD)
    private val mediumTyresButton =
      createButton("   Medium Tyres", "/tyres/mediumtyres.png", Tyre.MEDIUM)
    private val softTyresButton = createButton("   Soft Tyres", "/tyres/softtyres.png", Tyre.SOFT)
    private val tyresButtons = List(hardTyresButton, mediumTyresButton, softTyresButton)
    private val maxSpeedLabel = createLabel(
      "Select Maximum Speed (km/h):",
      Dimension(width, (height * 0.1).toInt),
      SwingConstants.CENTER,
      SwingConstants.BOTTOM
    )
    private val speedSelectedLabel = createLabel(
      minSpeed.toString,
      Dimension((width * 0.2).toInt, (height * 0.05).toInt),
      SwingConstants.CENTER,
      SwingConstants.CENTER
    )
    private val leftArrowButton = createArrowButton("/arrows/arrow-left.png", _ > minSpeed, _ - _)
    private val rightArrowButton = createArrowButton("/arrows/arrow-right.png", _ < maxSpeed, _ + _)
    private val starAttackLabel = createLabel(
      "Select Driver Attack Skills:",
      Dimension(width, (height * 0.1).toInt),
      SwingConstants.CENTER,
      SwingConstants.BOTTOM
    )
    private val starAttackButtons = createSkillsStarButtons(true)
    private val starDefenseLabel = createLabel(
      "Select Driver Defense Skills:",
      Dimension(width, (height * 0.1).toInt),
      SwingConstants.CENTER,
      SwingConstants.BOTTOM
    )
    private val starDefenseButtons = createSkillsStarButtons(false)
    private val initialRightPanel = createPanelAndAddAllComponents()

    initialRightPanel foreach (e => self.add(e))

    def updateParametersPanel(): Unit =
      tyresButtons.foreach(e => e.foreach(b => {
        if b.getName.equals(controller.currentCar.tyre.toString) then
          b.setBackground(colorSelected); b.setOpaque(true)
        else
          b.setBackground(colorNotSelected)
      }))
      speedSelectedLabel.foreach(e => e.setText(controller.currentCar.maxSpeed.toString))
      updateStar(starAttackButtons, controller.currentCar.driver.attack)
      updateStar(starDefenseButtons, controller.currentCar.driver.defense)

    private def createArrowButton(
                                   path: String,
                                   comparator: Int => Boolean,
                                   function: (Int, Int) => Int
                                 ): Task[JButton] =
      for
        button <- JButton(imageLoader.load(path))
        _ <- button.setBorder(BorderFactory.createEmptyBorder())
        _ <- button.setBackground(colorNotSelected)
        _ <- button.addActionListener(e => {
          if comparator(controller.currentCar.maxSpeed) then
            controller.setMaxSpeed(function(controller.currentCar.maxSpeed, 10))
            speedSelectedLabel.foreach(e => e.setText(controller.currentCar.maxSpeed.toString))
        })
      yield button

    private def createButton(text: String, fileName: String, tyre: Tyre): Task[JButton] =
      for
        button <- JButton(text, imageLoader.load(fileName))
        _ <- button.setName(tyre.toString)
        _ <-
          if tyre.equals(Tyre.HARD) then { button.setBackground(colorSelected); button.setOpaque(true) }
          else button.setBackground(colorNotSelected)
        _ <- button.setPreferredSize(Dimension((width * 0.3).toInt, (height * 0.09).toInt))
        _ <- button.addActionListener(e => {
          tyresButtons.foreach(e =>
            e.foreach(f => { f.getText match
              case b if button.getText.equals(f.getText) =>
                f.setBackground(colorSelected)
                f.setOpaque(true)
                controller.setTyre(tyre)
                controller.setPath(s"/cars/${controller.currentCarIndex}-${controller.currentCar.tyre.toString.toLowerCase}.png")
                controller.updateDisplayedCar()
              case _ => f.setBackground(colorNotSelected)
            })
          )
        })
      yield button

    private def createSkillsStarButtons(
                                         isAttack: Boolean
                                       ): List[Task[JButton]] =
      val buttons = for
        index <- 1 until 6
        button = createStarButton(index.toString, isAttack)
      yield button
      buttons.toList

    private def createStarButton(
                                  name: String,
                                  isAttack: Boolean
                                ): Task[JButton] =
      for
        button <-
          if name.equals("1") then JButton(imageLoader.load(fileNameSelected))
          else JButton(imageLoader.load(fileNameNotSelected))
        _ <- button.setBorder(BorderFactory.createEmptyBorder())
        _ <- button.setPreferredSize(Dimension((width * 0.09).toInt, (height * 0.08).toInt))
        _ <- button.setName(name)
        _ <- button.setBackground(colorNotSelected)
        _ <- button.addActionListener { e =>
          if isAttack then
            updateStar(starAttackButtons, button.getName.toInt)
            controller.setAttack(button.getName.toInt)
          else
            updateStar(starDefenseButtons, button.getName.toInt)
            controller.setDefense(button.getName.toInt)
        }
      yield button

    private def updateStar(
                            list: List[Task[JButton]],
                            index: Int
                          ): Unit =
      list.foreach(e =>
        e.foreach(f =>
          if f.getName.toInt <= index then f.setIcon(imageLoader.load(fileNameSelected))
          else f.setIcon(imageLoader.load(fileNameNotSelected))
        )
      )

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
        attackPanel <- JPanel(BorderLayout())
        _ <- attackPanel.add(starAttackLabel, BorderLayout.NORTH)
        attackStarPanel <- JPanel()
        defenseStarPanel <- JPanel()
        _ <- addStarsToPanel(starAttackButtons, attackStarPanel)
        _ <- attackPanel.add(attackStarPanel, BorderLayout.CENTER)
        defensePanel <- JPanel(BorderLayout())
        _ <- defensePanel.add(starDefenseLabel, BorderLayout.NORTH)
        _ <- addStarsToPanel(starDefenseButtons, defenseStarPanel)
        _ <- defensePanel.add(defenseStarPanel, BorderLayout.CENTER)
        _ <- panel.add(attackPanel)
        _ <- panel.add(defensePanel)
        _ <- panel.setVisible(true)
      yield panel

    private def addStarsToPanel(starAttackButtons: List[Task[JButton]], panel: JPanel): Task[Unit] =
      for task <- starAttackButtons do addBtn(task, panel)

    private def addBtn(task: Task[JButton], panel: JPanel): Task[Unit] =
      val p = for
        btn <- task
        _ <- panel.add(btn)
      yield ()
      p.runSyncUnsafe()
