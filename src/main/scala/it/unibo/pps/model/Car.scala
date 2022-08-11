package it.unibo.pps.model

case class Car(name: String, var tyre: Tyre, driver: Driver, var maxSpeed: Int)
/*
trait Car:
  def getName(): String
  def getTyreType(): Tyre
  def setTyreType(tyreType: Tyre): Unit

object Car:
  def apply(name: String, tyreType: Tyre, driver: Driver, maxSpeed: Int): Car = new CarImpl(name, tyreType, driver, maxSpeed)

  private class CarImpl(name: String, tyreType: Tyre, driver: Driver, maxSpeed: Int) extends Car:

    override def getName(): String = name
    override def getTyreType(): Tyre = tyreType

    override def setTyreType(tyre: Tyre): Unit = tyreType = tyre
*/