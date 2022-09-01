package it.unibo.pps.view

object Constants:

  sealed trait CommonConstants:
    val NUM_CARS = 4
    val FRAME_WIDTH = 1296
    val FRAME_HEIGHT = 810
    val CAR_NAMES = Map(0 -> "Ferrari", 1 -> "Mercedes", 2 -> "Red Bull", 3 -> "McLaren")

  object EndRacePanelConstants extends CommonConstants:
    val STANDINGS_PANEL_WIDTH = 900
    val STANDINGS_PANEL_HEIGHT = 400
    val STANDINGS_COMPONENT_HEIGHT: Int = (STANDINGS_PANEL_HEIGHT * 0.12).toInt
    val STANDINGS_COLOR_HEIGHT: Int = (STANDINGS_PANEL_HEIGHT * 0.1).toInt
    val STANDINGS_COLOR_WIDTH: Int = (STANDINGS_PANEL_WIDTH * 0.03).toInt
    val STANDINGS_POSITION_WIDTH: Int = (STANDINGS_PANEL_WIDTH * 0.02).toInt
    val STANDINGS_NAME_WIDTH: Int = (STANDINGS_PANEL_WIDTH * 0.11).toInt
    val STANDINGS_PADDING_WIDTH: Int = (STANDINGS_PANEL_WIDTH * 0.03).toInt
    val STANDINGS_TYRE_WIDTH: Int = (STANDINGS_PANEL_WIDTH * 0.07).toInt
    val STANDINGS_TIME_WIDTH: Int = (STANDINGS_PANEL_WIDTH * 0.06).toInt
