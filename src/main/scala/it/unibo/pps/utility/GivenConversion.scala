package it.unibo.pps.utility

import monix.eval.Task

import javax.swing.{JButton, JFrame, JPanel}

object GivenConversion:

  sealed trait CommonConversion:
    given Conversion[Unit, Task[Unit]] = Task(_)

  object GuiConversion extends CommonConversion:
    given Conversion[JFrame, Task[JFrame]] = Task(_)
    given Conversion[JPanel, Task[JPanel]] = Task(_)
    given Conversion[JButton, Task[JButton]] = Task(_)
