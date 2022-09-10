package it.unibo.pps.view.main_panel

import it.unibo.pps.controller.ControllerModule
import it.unibo.pps.model.Tyre
import it.unibo.pps.utility.GivenConversion.GuiConversion.given
import it.unibo.pps.view.Constants.ParamsSelectionPanelConstants.*
import monix.eval.{Task, TaskLift}
import monix.execution.Scheduler.Implicits.global
import it.unibo.pps.utility.PimpScala.RichJPanel.*

import java.awt.event.{ActionEvent, ActionListener, ItemEvent, ItemListener}
import java.awt.{
  BorderLayout,
  Color,
  Component,
  Dimension,
  FlowLayout,
  GridBagConstraints,
  GridBagLayout,
  LayoutManager
}
import javax.swing.*

trait ParamsSelectionPanel extends JPanel:

  /** Method that updates the displayed parameters when the car displayed is changed */
  def updateParametersPanel: Unit

object ParamsSelectionPanel:
  def apply(controller: ControllerModule.Controller): ParamsSelectionPanel =
    ParamsSelectionPanelImpl(controller)

  private class ParamsSelectionPanelImpl(controller: ControllerModule.Controller) extends ParamsSelectionPanel:
    self =>
    private val tyresLabel = createLabel(
      "Select tyres: ",
      Dimension(SELECTION_PANEL_WIDTH, TYRES_LABEL_HEIGHT),
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
      Dimension(SELECTION_PANEL_WIDTH, MAX_SPEED_HEIGHT),
      SwingConstants.CENTER,
      SwingConstants.BOTTOM
    )
    private val speedSelectedLabel = createLabel(
      CAR_MIN_SPEED.toString,
      Dimension(SPEED_SELECTED_WIDTH, TYRES_LABEL_HEIGHT),
      SwingConstants.CENTER,
      SwingConstants.CENTER
    )
    private val leftArrowButton = createArrowButton("/arrows/arrow-left.png", _ > CAR_MIN_SPEED, _ - _)
    private val rightArrowButton = createArrowButton("/arrows/arrow-right.png", _ < CAR_MAX_SPEED, _ + _)
    private val starSkillsLabel = createLabel(
      "Select Driver Skills:",
      Dimension(SELECTION_PANEL_WIDTH, MAX_SPEED_HEIGHT),
      SwingConstants.CENTER,
      SwingConstants.BOTTOM
    )
    private val starSkillsButton = createSkillsStarButtons
    private val initialRightPanel = createPanelAndAddAllComponents

    initialRightPanel foreach (e => self.add(e))

    def updateParametersPanel: Unit =
      tyresButtons.foreach(e =>
        e.foreach(b => {
          if b.getName.equals(controller.currentCar.tyre.toString) then
            b.setBackground(BUTTON_SELECTED_COLOR); b.setOpaque(true)
          else b.setBackground(BUTTON_NOT_SELECTED_COLOR)
        })
      )
      speedSelectedLabel.foreach(e => e.setText(controller.currentCar.maxSpeed.toString))
      updateStar(starSkillsButton, controller.currentCar.driver.skills)

    private def createArrowButton(
        path: String,
        comparator: Int => Boolean,
        function: (Int, Int) => Int
    ): Task[JButton] =
      for
        button <- JButton(ImageLoader.load(path))
        _ <- button.setBorder(BorderFactory.createEmptyBorder())
        _ <- button.setBackground(BUTTON_NOT_SELECTED_COLOR)
        _ <- button.addActionListener(e => {
          if comparator(controller.currentCar.maxSpeed) then
            controller.setMaxSpeed(function(controller.currentCar.maxSpeed, MAX_SPEED_STEP))
            speedSelectedLabel.foreach(e => e.setText(controller.currentCar.maxSpeed.toString))
        })
      yield button

    private def createButton(text: String, fileName: String, tyre: Tyre): Task[JButton] =
      for
        button <- JButton(text, ImageLoader.load(fileName))
        _ <- button.setName(tyre.toString)
        _ <-
          if tyre.equals(Tyre.SOFT) then { button.setBackground(BUTTON_SELECTED_COLOR); button.setOpaque(true) }
          else button.setBackground(BUTTON_NOT_SELECTED_COLOR)
        _ <- button.setPreferredSize(Dimension(TYRES_BUTTON_WIDTH, TYRES_BUTTON_HEIGHT))
        _ <- button.addActionListener(e => {
          tyresButtons.foreach(e =>
            e.foreach(f => {
              f.getText match
                case b if button.getText.equals(f.getText) =>
                  f.setBackground(BUTTON_SELECTED_COLOR)
                  f.setOpaque(true)
                  controller.setTyre(tyre)
                  controller.setPath(
                    s"/cars/${controller.currentCarIndex}-${controller.currentCar.tyre.toString.toLowerCase}.png"
                  )
                  controller.updateDisplayedCar
                case _ => f.setBackground(BUTTON_NOT_SELECTED_COLOR)
            })
          )
        })
      yield button

    private def createSkillsStarButtons: List[Task[JButton]] =
      val buttons = for
        index <- 0 until MAX_SKILL_STARS
        button = createStarButton((index + 1).toString)
      yield button
      buttons.toList

    private def createStarButton(
        name: String
    ): Task[JButton] =
      for
        button <-
          if name.equals("1") then JButton(ImageLoader.load("/stars/selected-star.png"))
          else JButton(ImageLoader.load("/stars/not-selected-star.png"))
        _ <- button.setBorder(BorderFactory.createEmptyBorder())
        _ <- button.setPreferredSize(Dimension(STARS_BUTTON_WIDTH, STARS_BUTTON_HEIGHT))
        _ <- button.setName(name)
        _ <- button.setBackground(BUTTON_NOT_SELECTED_COLOR)
        _ <- button.addActionListener { e =>
          updateStar(starSkillsButton, button.getName.toInt)
          controller.setSkills(button.getName.toInt)
        }
      yield button

    private def updateStar(
        list: List[Task[JButton]],
        index: Int
    ): Unit =
      list.foreach(e =>
        e.foreach(f =>
          if f.getName.toInt <= index then f.setIcon(ImageLoader.load("/stars/selected-star.png"))
          else f.setIcon(ImageLoader.load("/stars/not-selected-star.png"))
        )
      )

    private def createLabel(text: String, dim: Dimension, horizontal: Int, vertical: Int): Task[JLabel] =
      for
        label <- JLabel(text)
        _ <- label.setPreferredSize(dim)
        _ <- label.setHorizontalAlignment(horizontal)
        _ <- label.setVerticalAlignment(vertical)
      yield label

    private def createPanelAndAddAllComponents: Task[JPanel] =
      for
        panel <- JPanel()
        _ <- panel.setPreferredSize(Dimension(SELECTION_PANEL_WIDTH, SELECTION_PANEL_HEIGHT))
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
        starSkillsLabel <- starSkillsLabel
        _ <- panel.addAll(
          List(tyresLabel, hardTyresButton, mediumTyresButton, softTyresButton, maxSpeedLabel, leftArrowButton,
            speedSelectedLabel, rightArrowButton)
        )
        skillsPanel <- JPanel(BorderLayout())
        _ <- skillsPanel.add(starSkillsLabel, BorderLayout.NORTH)
        skillsStarPanel <- JPanel()
        _ <- addStarsToPanel(starSkillsButton, skillsStarPanel)
        _ <- skillsPanel.add(skillsStarPanel, BorderLayout.CENTER)
        _ <- panel.add(skillsPanel)
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
