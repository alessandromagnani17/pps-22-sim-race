package it.unibo.pps.model

import it.unibo.pps.model.car.Car

/** Represents one snapshot of the simulation */
case class Snapshot(cars: List[Car], time: Int)
