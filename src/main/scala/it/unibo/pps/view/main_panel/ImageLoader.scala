package it.unibo.pps.view.main_panel

import it.unibo.pps.view.main_panel.ImageLoader

import javax.swing.ImageIcon

object ImageLoader:
  def load(file: String): ImageIcon = ImageIcon(ImageLoader.getClass.getResource(file))
