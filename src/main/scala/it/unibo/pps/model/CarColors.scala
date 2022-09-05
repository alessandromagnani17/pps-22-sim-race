package it.unibo.pps.model

import java.awt.Color

object CarColors:
  def getColor(car: String): Color = car match
    case "Ferrari" => Color(220, 10, 10)
    case "Mercedes" => Color(0, 210, 190)
    case "Red Bull" => Color(22, 24, 95)
    case "McLaren" => Color(255, 135, 0)