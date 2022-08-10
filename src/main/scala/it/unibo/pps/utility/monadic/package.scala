package it.unibo.pps.utility

import monix.eval.Task

package object monadic:

  /** Method that wraps a generic computation into a Monix Task
    *
    * @param e
    *   Represent a generic and possible async/lazy computation
    * @return
    *   A Task that wraps the given computation
    *
    * It is a facade, it is used to improve code writing
    */
  def io[E](e: => E): Task[E] = Task(e)
