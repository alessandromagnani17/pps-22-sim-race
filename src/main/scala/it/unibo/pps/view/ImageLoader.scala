package it.unibo.pps.view

import javax.swing.ImageIcon

trait ImageLoader:
  def load(file: String): ImageIcon

object ImageLoader:
  def apply(): ImageLoader = new ImageLoaderImpl()

  private class ImageLoaderImpl() extends ImageLoader:
    override def load(file: String): ImageIcon = ImageIcon(ImageLoader.getClass.getResource(file))
