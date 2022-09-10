package it.unibo.pps.utility

import it.unibo.pps.model.Snapshot
import alice.tuprolog.{Term, Theory}
import it.unibo.pps.model.car.{Car, CarColors, Tyre}
import it.unibo.pps.model.track.Direction
import monix.eval.Task

import java.awt.image.BufferedImage
import java.awt.{Color, Component, GridBagConstraints}
import javax.swing.{JButton, JComboBox, JFrame, JLabel, JPanel, JScrollPane}
import it.unibo.pps.view.charts.LineChart
import it.unibo.pps.view.simulation_panel.Environment
import org.jfree.chart.ChartPanel

object GivenConversion:

  sealed trait CommonConversion:
    given Conversion[Unit, Task[Unit]] = Task(_)
    given Conversion[Int, Task[Int]] = Task(_)
    given Conversion[Double, Task[Double]] = Task(_)

  object GuiConversion extends CommonConversion:
    given Component2Task[E <: Component]: Conversion[E, Task[E]] = Task(_)
    given Conversion[LineChart, Task[LineChart]] = Task(_)
    given Conversion[ChartPanel, Task[ChartPanel]] = Task(_)
    given Iterable2Task[E]: Conversion[Iterable[E], Task[Iterable[E]]] = Task(_)

  object ModelConversion extends CommonConversion:
    given Conversion[Car, Task[Car]] = Task(_)
    given Conversion[Snapshot, Task[Snapshot]] = Task(_)

  object LoaderGivenConversion:
    given Itearable2List[E]: Conversion[Iterable[E], List[E]] = _.toList
    given Conversion[String, Term] = Term.createTerm(_)
    given Conversion[Seq[_], Term] = _.mkString("[", ",", "]")
    given Conversion[String, Theory] = Theory.parseLazilyWithStandardOperators(_)
    given Conversion[String, Int] = Integer.parseInt(_)

  object CarLoaderGivenConversion:
    given Conversion[String, Tyre] = _ match
      case s: String if s.equals("Soft") => Tyre.SOFT
      case s: String if s.equals("Medium") => Tyre.MEDIUM
      case s: String if s.equals("Hard") => Tyre.HARD

    given Conversion[String, Color] = _ match
      case s: String if s.equals("Ferrari") => CarColors.getColor(s)
      case s: String if s.equals("Mercedes") => CarColors.getColor(s)
      case s: String if s.equals("Red Bull") => CarColors.getColor(s)
      case s: String if s.equals("McLaren") => CarColors.getColor(s)

  object DirectionGivenConversion:
    given Conversion[Direction, Int] = _ match
      case Direction.Forward => 1
      case Direction.Backward => -1

    given Conversion[String, Direction] = _.toInt match
      case d if d == 1 => Direction.Forward
      case d if d == -1 => Direction.Backward
