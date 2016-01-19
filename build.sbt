//import AssemblyKeys._ // put this at the top of the file

//You can check configuration examples here:
//http://www.scala-sbt.org/release/docs/Examples/Quick-Configuration-Examples

//assemblySettings

//jarName in assembly := "scala-log-analyser.jar"

//mainClass in assembly := Some("Sessionizer")

name := "scala-log-analyser"

organization := "ddt.log.analyser"

version := "0.0.1-SNAPSHOT"

description := "An Apache Log Analyser"

scalaVersion := "2.11.7"

scalacOptions ++= Seq( "-deprecation", "-unchecked", "-feature" )

fork := true

javaOptions ++= Seq(
"-Dfiles.directory=hpa-data"
)

libraryDependencies ++= Seq(
  "org.scalatest" % "scalatest_2.11" % "2.2.6" % "test",
  "junit" % "junit" % "4.11" % "test",
  "com.novocode" % "junit-interface" % "0.7" % "test->default"
)

publishMavenStyle := true

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

pomExtra :=
<licenses>
  <license>
    <name>Apache 2</name>
    <url>http://www.apache.org/licenses/LICENSE-2.0.txt</url>
    <distribution>repo</distribution>
  </license>
</licenses>

crossPaths := false
