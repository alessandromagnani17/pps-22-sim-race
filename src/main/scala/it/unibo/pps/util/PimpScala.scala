package it.unibo.pps.util

object PimpScala:

  object RichInt:
    extension (i: Int)
      def **(exp: Int): Int = Math.pow(i, exp).toInt
      def root: Int = Math.sqrt(i).toInt

  object RichTuple2:
    import RichInt._
    extension (p0: Tuple2[Int, Int])
      def euclideanDistance(p1: Tuple2[Int, Int]): Int =
        ((p1._1 - p0._1) ** 2 + (p1._2 - p0._2) ** 2).root
