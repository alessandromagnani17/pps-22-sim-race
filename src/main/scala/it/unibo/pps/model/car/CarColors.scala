package it.unibo.pps.model.car

import java.awt.Color

object CarColors:
  /** Method that returns the color of a specific car name
    * @param car
    *   The name of the car to return the color of
    */
  def getColor(car: String): Color = car match
    case "Ferrari" => Color(220, 10, 10)
    case "Mercedes" => Color(0, 210, 190)
    case "Red Bull" => Color(22, 24, 95)
    case "McLaren" => Color(255, 135, 0)
