import Dependencies._

lazy val root = (project in file("."))
  .settings(
    name := "doddle",
    version := "0.0.1",
    scalaVersion := "2.12.4",
    libraryDependencies ++= loggingDeps ++ testingDeps ++ breeze
  )
