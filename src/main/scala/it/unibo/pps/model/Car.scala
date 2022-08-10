package it.unibo.pps.model

trait Car:
  def getName(): String
  def getTyreType(): Tyre

object Car:
  def apply(name: String, tyreType: Tyre, driver: Driver, maxSpeed: Int): Car = new CarImpl(name, tyreType, driver, maxSpeed)

  private class CarImpl(name: String, tyreType: Tyre, driver: Driver, maxSpeed: Int) extends Car:

    override def getName(): String = name

    override def getTyreType(): Tyre = tyreType
