package it.unibo.pps

package object model:
  given Conversion[Tyre, Int] = _ match
    case Tyre.SOFT => 0
    case Tyre.MEDIUM => 15
    case Tyre.HARD => 30
