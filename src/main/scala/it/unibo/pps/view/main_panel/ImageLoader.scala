package it.unibo.pps.view.main_panel

import it.unibo.pps.view.main_panel.ImageLoader

import javax.swing.ImageIcon

object ImageLoader:

  /** Methods that transform a path into an ImageIcon
   * @param file
   *   the filepath of the image to transform into an ImageIcon
   */
  def load(file: String): ImageIcon = ImageIcon(ImageLoader.getClass.getResource(file))
