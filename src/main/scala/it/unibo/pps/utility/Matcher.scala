package it.unibo.pps.utility

import it.unibo.pps.model.Tyre

trait Matcher:
  def matcher(tyre: String): Tyre

object Matcher:
  def apply(): Matcher = new MatcherImpl()

  private class MatcherImpl() extends Matcher:
    override def matcher(tyre: String): Tyre = tyre match
      case "HARD" => Tyre.HARD
      case "MEDIUM" => Tyre.MEDIUM
      case "SOFT" => Tyre.SOFT
