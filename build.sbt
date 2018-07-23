name := "pp-sql"

version := "1.0-SNAPSHOT"

scalaVersion := "2.12.6"

val slf4jVer = "1.7.25"

libraryDependencies ++= Seq(
  "javax.transaction" % "jta" % "1.1",
  "org.slf4j" % "slf4j-api" % slf4jVer,
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.0",
  "org.slf4j" % "slf4j-simple" % slf4jVer % "test",
  "org.scalatest" %% "scalatest" % "3.0.5" % "test",
  "com.h2database" % "h2" % "1.4.197" % "test"
)