package it.unibo.pps.prolog

import alice.tuprolog.{Prolog, SolveInfo, Struct, Term, Theory}

object Scala2P:

  /** The prolog engine for solving goals from the given theory.
    * @param theory
    *   the theory used for solving goal
    * @return
    *   a [[Iterable]] of [[SolveInfo]]
    */
  def createEngine(theory: String): Term => Iterable[SolveInfo] =
    prologEngine(
      Theory.parseLazilyWithStandardOperators(
        getClass.getResourceAsStream(theory)
      )
    )

  private def extractTerm(t: Term, i: Int): Term =
    t.asInstanceOf[Struct].getArg(i).getTerm

  /** Extracts a Term and converts it to a String
    * @param solveInfo
    *   The solve info
    * @param s
    *   The term to extract
    * @return
    *   The extracted term
    */
  def extractTermToString(solveInfo: SolveInfo, s: String): String =
    solveInfo.getTerm(s).toString.replace("'", "")

  /** Extracts a list of Terms and converts it to a String
    * @param solveInfo
    *   The solve info
    * @param s
    *   The terms to extract
    * @return
    *   The extracted terms
    */
  def extractTermsToListOfStrings(solveInfo: SolveInfo, s: List[String]): List[String] =
    s.map(extractTermToString(solveInfo, _))

  private def prologEngine(theory: Theory): Term => Iterable[SolveInfo] =
    val engine = Prolog()
    engine.setTheory(theory)
    goal =>
      new Iterable[SolveInfo]:
        override def iterator: Iterator[SolveInfo] = new Iterator[SolveInfo]:
          var solution: Option[SolveInfo] = Some(engine.solve(goal))

          override def hasNext: Boolean =
            solution.fold(false)(f => f.hasOpenAlternatives || f.isSuccess)

          override def next(): SolveInfo =
            try solution.get
            finally
              solution =
                if (solution.get.hasOpenAlternatives) Some(engine.solveNext())
                else None
