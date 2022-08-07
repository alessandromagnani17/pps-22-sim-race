package it.unibo.pps.prolog

import alice.tuprolog.{Prolog, SolveInfo, Struct, Term, Theory}

object Scala2P:

  def createEngine(theory: String): Term => Iterable[SolveInfo] =
    prologEngine(
      Theory.parseLazilyWithStandardOperators(
        getClass.getResourceAsStream(theory)
      )
    )

  private def extractTerm(t: Term, i: Int): Term =
    t.asInstanceOf[Struct].getArg(i).getTerm

  def extractTermToString(solveInfo: SolveInfo, s: String): String =
    solveInfo.getTerm(s).toString.replace("'", "")
  
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
