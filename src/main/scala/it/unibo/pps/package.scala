package it.unibo

import it.unibo.pps.model
import it.unibo.pps.model.Direction

package object pps:
  given Conversion[Direction, Int] = _ match
    case Direction.Forward => 1
    case Direction.Backward => -1
