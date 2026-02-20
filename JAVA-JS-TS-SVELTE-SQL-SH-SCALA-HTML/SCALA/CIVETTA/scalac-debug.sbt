name := """seed1"""
organization := "testing"
version := "1.0-SNAPSHOT"
lazy val root = (project in file(".")).enablePlugins(PlayScala)
scalaVersion := "2.13.8"
libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test
libraryDependencies += ws
// ok: scalac-debug
scalacOptions ++= Seq(
  "-no-specialization"
)
// ruleid: scalac-debug
scalacOptions ++= Seq(
  "-encoding", "utf8", 
  "-Xfatal-warnings",  
  "-deprecation",
  "-Vdebug", // VIOLAZ
  "-language:implicitConversions",
  "-language:higherKinds",
  "-language:existentials",
  "-language:postfixOps"
)
// ok
scalacOptions ++= Seq(
  "-Xsource-reader", "CLASSNAME",
  "-opt-inline-from", "PATTERNS1"
)
// VIOLAZ 
scalacOptions += "-Ydebug"
