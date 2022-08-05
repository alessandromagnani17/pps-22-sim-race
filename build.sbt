name := "pps-22-sim-race"

version := "0.1"

scalaVersion := "3.1.3"

//Add Monix dependencies
libraryDependencies += "io.monix" %% "monix" % "3.4.1"

//Add JFreeChart dependencies
libraryDependencies += "org.jfree" % "jfreechart" % "1.5.3"

//Add ScalaTest dependencies
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.12" % Test

//Add Prolog dependencies
libraryDependencies += "it.unibo.alice.tuprolog" % "2p-core" % "4.1.1"
libraryDependencies += "it.unibo.alice.tuprolog" % "2p-ui" % "4.1.1"