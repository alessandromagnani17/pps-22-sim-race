package it.unibo.pps.view

import java.awt.Color

object Constants:

  sealed trait CommonConstants:
    val FRAME_WIDTH = 1296
    val FRAME_HEIGHT = 810
    val BUTTON_NOT_SELECTED_COLOR: Color = Color(238, 238, 238)
    val BUTTON_SELECTED_COLOR: Color = Color(79, 195, 247)
    val NUM_CARS = 4
    val CAR_NAMES = Map(0 -> "Ferrari", 1 -> "Mercedes", 2 -> "Red Bull", 3 -> "McLaren")
    val CAR_MAX_SPEED = 350
    val CAR_MIN_SPEED = 200
    val SELECTION_PANEL_WIDTH: Int = (FRAME_WIDTH * 0.48).toInt
    val SELECTION_PANEL_HEIGHT: Int = (FRAME_HEIGHT * 0.65).toInt
    val START_PANEL_HEIGHT: Int = FRAME_HEIGHT - SELECTION_PANEL_HEIGHT

  object MainPanelConstants extends CommonConstants

  object CarSelectionPanelConstants extends CommonConstants:
    val CAR_SELECTED_HEIGHT: Int = (SELECTION_PANEL_HEIGHT * 0.2).toInt
    val CAR_IMAGE_HEIGHT: Int = (SELECTION_PANEL_HEIGHT * 0.35).toInt

  object ParamsSelectionPanelConstants extends CommonConstants:
    val TYRES_LABEL_HEIGHT: Int = (SELECTION_PANEL_HEIGHT * 0.05).toInt
    val MAX_SPEED_HEIGHT: Int = (SELECTION_PANEL_HEIGHT * 0.1).toInt
    val SPEED_SELECTED_WIDTH: Int = (SELECTION_PANEL_WIDTH * 0.2).toInt
    val TYRES_BUTTON_WIDTH: Int = (SELECTION_PANEL_WIDTH * 0.3).toInt
    val TYRES_BUTTON_HEIGHT: Int = (SELECTION_PANEL_HEIGHT * 0.09).toInt
    val STARS_BUTTON_WIDTH: Int = (SELECTION_PANEL_WIDTH * 0.09).toInt
    val STARS_BUTTON_HEIGHT: Int = (SELECTION_PANEL_HEIGHT * 0.08).toInt

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
