package it.unibo.pps.view

import it.unibo.pps.controller.ControllerModule
import it.unibo.pps.utility.GivenConversion.GuiConversion.given
import monix.eval.Task

import java.awt.{BorderLayout, Color, Component, Dimension, FlowLayout, GridBagConstraints, GridBagLayout, LayoutManager}
import javax.swing.{BorderFactory, DefaultListCellRenderer, ImageIcon, JButton, JComboBox, JLabel, JList, JPanel, SwingConstants}
import javax.swing.JPanel
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
    private val hardTyresButton = createJButton("   Hard Tyres", "src/main/resources/tyres/hardtyres.png")
    private val mediumTyresButton = createJButton("   Medium Tyres", "src/main/resources/tyres/mediumtyres.png")
    private val softTyresButton = createJButton("   Soft Tyres", "src/main/resources/tyres/softtyres.png")

    private val tyresButtons = List(hardTyresButton, mediumTyresButton, softTyresButton)

    private val lapsLabel = createJLabel("Select laps:")
    private val rightArrowButton = createRightArrowButton("src/main/resources/arrow-right.png")
    private val leftArrowButton = createLeftArrowButton("src/main/resources/arrow-left.png")
    private val lapsSelectedLabel = createJLabel("1")

    private val colorNotSelected = Color(238, 238, 238)
    private val colorSelected = Color(79, 195, 247)

    initialRightPanel foreach(e => self.add(e))


    // inizio aggiunte (le informazioni come setBackground o setPreferredSize le metterei sotto)
    private def createJButton(text: String, fileName: String): Task[JButton] =
      for
        button <- JButton(text, ImageIcon(fileName))
        _ <- button.setBackground(colorNotSelected)
        _ <- button.setPreferredSize(Dimension((width * 0.3).toInt, (height * 0.07).toInt))
        _ <- button.addActionListener(new ActionListener {
          override def actionPerformed(e: ActionEvent): Unit = tyresButtons.foreach(e => e.foreach(f => { f.getName == button.getName; f.setBackground(colorNotSelected); button.setBackground(colorSelected) }))
        })
      yield button

    private def createRightArrowButton(filename: String): Task[JButton] =
      for
        button <- JButton(ImageIcon(filename))
        _ <- button.setBackground(colorNotSelected)
        _ <- button.addActionListener(new ActionListener {
          override def actionPerformed(e: ActionEvent): Unit = { lapsSelectedLabel.foreach(e => e.setText((e.getText.toInt + 1).toString)); leftArrowButton.foreach(e => e.setEnabled(true)) }
        })
      yield button

    private def createLeftArrowButton(filename: String): Task[JButton] =
      for
        button <- JButton(ImageIcon(filename))
        _ <- button.setEnabled(false)
        _ <- button.setBackground(colorNotSelected)
        _ <- button.addActionListener(new ActionListener {
          override def actionPerformed(e: ActionEvent): Unit =  lapsSelectedLabel.foreach(e => { e.setText((e.getText.toInt - 1).toString); println((e.getText.toInt - 1).toString) } )
        })
      yield button

      //lapsSelectedLabel.foreach(e => { e.setText((e.getText.toInt - 1).toString); if e.getText.toInt == 1 then button.setEnabled(false) })

      /* for
lapsSelectedLabel <- lapsSelectedLabel
_ <- lapsSelectedLabel.setText((lapsSelectedLabel.getText.toInt - 1).toString)
_ <- if lapsSelectedLabel.getText.toInt == 1 then button.setEnabled(false)
yield */
    // fine aggiunte




    private def createJLabel(text: String): Task[JLabel] =
      for
        jl <- JLabel(text)
      yield jl


    private def createPanel(): Task[JPanel] =
      for
        panel <- JPanel()
        _ <- panel.setPreferredSize(Dimension(width, height))
        _ <- panel.setLayout(FlowLayout())

        tyresLabel <- tyresLabel
        _ <- tyresLabel.setPreferredSize(Dimension(width, (height * 0.05).toInt))
        _ <- tyresLabel.setHorizontalAlignment(SwingConstants.CENTER)
        _ <- tyresLabel.setVerticalAlignment(SwingConstants.BOTTOM)

        hardTyresButton <- hardTyresButton
        mediumTyresButton <- mediumTyresButton
        softTyresButton <- softTyresButton

        lapsLabel <- lapsLabel
        _ <- lapsLabel.setPreferredSize(Dimension(width, (height * 0.1).toInt))
        _ <- lapsLabel.setHorizontalAlignment(SwingConstants.CENTER)
        _ <- lapsLabel.setVerticalAlignment(SwingConstants.BOTTOM)

        leftArrowButton <- leftArrowButton

        lapsSelectedLabel <- lapsSelectedLabel
        _ <- lapsSelectedLabel.setPreferredSize(Dimension((width * 0.2).toInt, (height * 0.05).toInt))
        _ <- lapsSelectedLabel.setHorizontalAlignment(SwingConstants.CENTER)
        _ <- lapsSelectedLabel.setVerticalAlignment(SwingConstants.CENTER)

        rightArrowButton <- rightArrowButton


        _ <- panel.add(tyresLabel)
        _ <- panel.add(hardTyresButton)
        _ <- panel.add(mediumTyresButton)
        _ <- panel.add(softTyresButton)
        _ <- panel.add(lapsLabel)
        _ <- panel.add(leftArrowButton)
        _ <- panel.add(lapsSelectedLabel)
        _ <- panel.add(rightArrowButton)
        _ <- panel.setVisible(true)
      yield panel