package it.unibo.pps.model

trait Car

object Car:
  def apply(name: String, tyreType: Tyre, driver: Driver, maxSpeed: Int): Car = new CarImpl(name, tyreType, driver, maxSpeed)

  private class CarImpl(name: String, tyreType: Tyre, driver: Driver, maxSpeed: Int) extends Car:
    private var x = 5
