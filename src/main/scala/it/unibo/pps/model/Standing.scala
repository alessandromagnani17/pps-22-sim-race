package it.unibo.pps.model

import scala.collection.mutable.Map

trait Standing:

  /** @return the current standing */
  def _standing: Map[Int, Car]

  /** Method that updates the standing after an overtake happened */
  def overtake(): Unit

object Standing:
  def apply(standing: Map[Int, Car]): Standing = new StadingImpl(standing)

  private class StadingImpl(standing: Map[Int, Car]) extends Standing:

    override def _standing: Map[Int, Car] = standing
    override def overtake(): Unit = ???
