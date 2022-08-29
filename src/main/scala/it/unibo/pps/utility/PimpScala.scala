package it.unibo.pps.utility

import scala.collection.immutable.HashMap
import javax.swing.JPanel
import java.awt.Component

object PimpScala:

  object RichInt:
    extension (i: Int)
      def **(exp: Int): Int = Math.pow(i, exp).toInt
      def root: Int = Math.sqrt(i).toInt

  object RichTuple2:
    import RichInt.*
    extension (p0: Tuple2[Int, Int])
      def euclideanDistance(p1: Tuple2[Int, Int]): Int =
        ((p1._1 - p0._1) ** 2 + (p1._2 - p0._2) ** 2).root

  object RichOption:
    extension [A](o: Option[A])
      /** Method that applies a consumer to the [[Option]]
        *
        * If the Option is empty it does nothing
        */
      def -->(consumer: A => Unit): Unit = o foreach consumer

  object RichHashMap:
    import RichOption.*
    extension [A, B](h: HashMap[A, B])
      /** Method that applies a consumer to the element of the [[HashMap]] pointed by the specified key
        *
        * If the element doesn't exists it does nothing
        */
      def ?-->(key: A, consumer: B => Unit): Unit = h.get(key) --> consumer

  object RichJPanel:
    extension (p: JPanel)
      def addAll[E <: Component](elements: List[E]): Unit =
        elements.foreach(p.add(_))
