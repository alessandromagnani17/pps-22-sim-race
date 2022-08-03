package it.unibo.pps.util

object PimpScala:

  object RichInt:
    extension (i: Int)
      def **(exp: Int): Int = Math.pow(i, exp).toInt
      def root(): Int = Math.sqrt(i).toInt
