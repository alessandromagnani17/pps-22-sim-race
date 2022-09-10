package it.unibo.pps.model

import it.unibo.pps.model.car.Car

case class Snapshot(cars: List[Car], time: Int)
