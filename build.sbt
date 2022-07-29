name := "pps-22-sim-race"

version := "0.1"

scalaVersion := "3.1.3"

//Add Monix dependencies
libraryDependencies += "io.monix" %% "monix" % "3.4.1"

//Add XChart dependencies
libraryDependencies += "org.knowm.xchart" % "xchart" % "3.8.0" exclude ("de.erichseifert.vectorgraphics2d", "VectorGraphics2D") withSources ()
