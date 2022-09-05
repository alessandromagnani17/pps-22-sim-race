package it.unibo

import it.unibo.pps.model
import it.unibo.pps.model.Direction

package object pps:
  given Conversion[Direction, Int] = _ match
    case Direction.Forward => 1
    case Direction.Backward => -1

  given Conversion[String, Direction] = _.toInt match
    case d if d == 1 => Direction.Forward
    case d if d == -1 => Direction.Backward
