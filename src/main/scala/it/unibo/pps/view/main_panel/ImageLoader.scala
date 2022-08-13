package it.unibo.pps.view.main_panel

import it.unibo.pps.view.ImageLoader

import javax.swing.ImageIcon

trait ImageLoader:
  def load(file: String): ImageIcon

object ImageLoader:
  def apply(): ImageLoader = new ImageLoaderImpl()

  private class ImageLoaderImpl() extends ImageLoader:
    override def load(file: String): ImageIcon = ImageIcon(ImageLoader.getClass.getResource(file))
