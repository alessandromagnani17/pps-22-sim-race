package it.unibo.pps.model.loader

trait Loader:
  type E
  def load: E
