package it.unibo.pps.view

object Constants:

  sealed trait CommonConstants:
    val NUM_CARS = 4
    val FRAME_WIDTH = 1296
    val FRAME_HEIGHT = 810
    
  object EndRacePanelConstants extends CommonConstants:
    val FINAL_STANDING_PANEL_WIDTH = 900
    val FINAL_STANDING_PANEL_HEIGHT = 400
