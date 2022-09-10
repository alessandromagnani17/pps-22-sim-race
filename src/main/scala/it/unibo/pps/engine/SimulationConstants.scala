package it.unibo.pps.engine

object SimulationConstants:
  val BASE_TIME: Double = 500
  val DEFAULT_SPEED: Double = 0.6
  val LOW_SPEED: Double = 1.5
  val HIGH_SPEED: Double = 0.1
  val EMPTY_POSITION = (0, 0)
  val BASE_SECTORTIME_TURN = 3
  val BASE_SECTORTIME_STRAIGHT = 55
  val CAR_NAMES = Map(0 -> "Ferrari", 1 -> "Mercedes", 2 -> "Red Bull", 3 -> "McLaren")
