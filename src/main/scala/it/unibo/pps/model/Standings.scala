package it.unibo.pps.model

import scala.collection.mutable.Map

trait Standings:

  /** @return the current standing */
  def _standings: Map[Int, Car]

  /** Method that updates the standing after an overtake happened */
  def overtake(): Unit

object Standings:
  def apply(standings: Map[Int, Car]): Standings = new StadingImpl(standings)

  private class StadingImpl(standings: Map[Int, Car]) extends Standings:

    override def _standings: Map[Int, Car] = standings
    override def overtake(): Unit = ???
