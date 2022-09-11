package it.unibo.pps.model.loader

/** Loads data from a prolog file */
trait Loader:
  type E
  def load: E
