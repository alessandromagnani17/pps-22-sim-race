package it.unibo.pps.utility

import it.unibo.pps.model.{Car, Standings}

import scala.math.BigDecimal

object UtilityFunctions:

  /** Returns a time converted in minutes/seconds format from virtual time
   * @param time
   *   The virtual time to be converted
   */
  def convertTimeToMinutes(time: Int): String =
    val minutes: Int = time / 60
    val seconds: Double = time % 60
    BigDecimal(minutes + seconds / 100).setScale(2, BigDecimal.RoundingMode.HALF_EVEN).toString.replace(".", ":")

  /** Returns the gap from the leader car or the converted race time
   * @param car
   *   The car on which to calculate the gap
   */
  def calcGapToLeader(car: Car, standings: Standings): String =
    if standings.standings.head.equals(car) then convertTimeToMinutes(car.raceTime)
    else
      val gap = car.raceTime - standings.standings.head.raceTime
      if gap > 0 then s"+${convertTimeToMinutes(gap)}"
      else "+00:00"

    
