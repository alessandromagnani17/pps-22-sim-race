package it.unibo.pps.model

enum Sector:
  case Straight(id: Int, initialX: Int, initialY: Int, finalX: Int, finalY: Int)
  case Turn()
