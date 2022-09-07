package it.unibo.pps.model

import scala.collection.mutable.Map

trait Standings:

  /** @return the current standing */
  def _standings: List[Car]

object Standings:
  def apply(standings: List[Car]): Standings = new StandingsImpl(standings)

  private class StandingsImpl(standings: List[Car]) extends Standings:

    override def _standings: List[Car] = standings