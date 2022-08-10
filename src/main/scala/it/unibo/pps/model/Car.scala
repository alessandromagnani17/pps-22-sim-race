package it.unibo.pps.model

trait Car

object Car:
  def apply(): Car = new CarImpl("", Tyre.HARD, null, 0)

  private class CarImpl(name: String, tyreType: Tyre, driver: Driver, maxSpeed: Int) extends Car:
    private var x = 5
