import Dependencies._

lazy val root = (project in file("."))
  .settings(
    name := "doddle-model",
    organization := "com.picnicml",
    version := "0.0.0",
    scalaVersion := "2.12.4",
    libraryDependencies ++= loggingDeps ++ testingDeps ++ breeze
  )
