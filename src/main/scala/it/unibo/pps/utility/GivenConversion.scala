package it.unibo.pps.utility

import monix.eval.Task

import java.awt.image.BufferedImage
import java.awt.{Component, GridBagConstraints}
import javax.swing.{JButton, JComboBox, JFrame, JLabel, JPanel, JScrollPane}
import it.unibo.pps.view.Enviroment
import it.unibo.pps.view.charts.LineChart
import org.jfree.chart.ChartPanel

object GivenConversion:

  sealed trait CommonConversion:
    given Conversion[Unit, Task[Unit]] = Task(_)
    given Conversion[Int, Task[Int]] = Task(_)

  object GuiConversion extends CommonConversion:
    given Conversion[JFrame, Task[JFrame]] = Task(_)
    given Conversion[JPanel, Task[JPanel]] = Task(_)
    given Conversion[JLabel, Task[JLabel]] = Task(_)
    given Conversion[JButton, Task[JButton]] = Task(_)
    given Conversion[JComboBox[String], Task[JComboBox[String]]] = Task(_)
    given Conversion[Component, Task[Component]] = Task(_)
    given Conversion[BufferedImage, Task[BufferedImage]] = Task(_)
    given Conversion[GridBagConstraints, Task[GridBagConstraints]] = Task(_)
    given Conversion[Enviroment, Task[Enviroment]] = Task(_)
    given Conversion[JScrollPane, Task[JScrollPane]] = Task(_)
    given Conversion[LineChart, Task[LineChart]] = Task(_)
    given Conversion[ChartPanel, Task[ChartPanel]] = Task(_)
