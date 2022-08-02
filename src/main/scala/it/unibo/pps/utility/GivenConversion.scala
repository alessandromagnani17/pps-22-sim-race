package it.unibo.pps.utility

import monix.eval.Task

import javax.swing.{JFrame, JPanel}

object GivenConversion:

  sealed trait CommonConversion:
    given Conversion[Unit, Task[Unit]] = Task(_)

  object GuiConversion extends CommonConversion:
    given Conversion[JFrame, Task[JFrame]] = Task(_)
    given Conversion[JPanel, Task[JPanel]] = Task(_)
