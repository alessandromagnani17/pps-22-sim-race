package it.unibo.pps.utility

import it.unibo.pps.model.{Car, Snapshot}
import alice.tuprolog.{Term, Theory}
import monix.eval.Task

import java.awt.image.BufferedImage
import java.awt.{Component, GridBagConstraints}
import javax.swing.{JButton, JComboBox, JFrame, JLabel, JPanel, JScrollPane}
import it.unibo.pps.view.charts.LineChart
import it.unibo.pps.view.simulation_panel.Enviroment
import org.jfree.chart.ChartPanel

object GivenConversion:

  sealed trait CommonConversion:
    given Conversion[Unit, Task[Unit]] = Task(_)
    given Conversion[Int, Task[Int]] = Task(_)
    given Conversion[Double, Task[Double]] = Task(_)

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


  object ModelConversion extends CommonConversion:
    given Conversion[Car, Task[Car]] = Task(_)
    given Conversion[Snapshot, Task[Snapshot]] = Task(_)


  object TrackBuilderGivenConversion:
    given Itearable2List[E]: Conversion[Iterable[E], List[E]] = _.toList
    given Conversion[String, Term] = Term.createTerm(_)
    given Conversion[Seq[_], Term] = _.mkString("[", ",", "]")
    given Conversion[String, Theory] = Theory.parseLazilyWithStandardOperators(_)
    given Conversion[String, Int] = Integer.parseInt(_)
