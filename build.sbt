ThisBuild / version := "0.1"
ThisBuild / organization := "it.unibo.ppp.sim-race"

name := "pps-22-sim-race"

scalaVersion := "3.1.3"

lazy val app = (project in file("app"))
  .settings(
    assembly / mainClass := Some("it.unibo.pps.launcher.Launcher")
  )

lazy val utils = (project in file("utils"))
  .settings(
    assembly / assemblyJarName := s"sim-race-$version.jar"
  )

ThisBuild / assemblyMergeStrategy := {
  case PathList("META-INF", _*) => MergeStrategy.discard
  case _ => MergeStrategy.first
}

//Add Monix dependencies
libraryDependencies += "io.monix" %% "monix" % "3.4.1"

//Add JFreeChart dependencies
libraryDependencies += "org.jfree" % "jfreechart" % "1.5.3"

//Add ScalaTest dependencies
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.12" % Test

//Add Prolog dependencies
libraryDependencies += "it.unibo.alice.tuprolog" % "2p-core" % "4.1.1"
libraryDependencies += "it.unibo.alice.tuprolog" % "2p-ui" % "4.1.1"
