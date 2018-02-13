import sbt._

object Dependencies {

  val loggingDeps: Seq[ModuleID] =
    "com.typesafe.scala-logging" %% "scala-logging" % "3.7.2" ::
    "ch.qos.logback" % "logback-classic" % "1.2.3" :: Nil

  val testingDeps: Seq[ModuleID] =
    "org.scalatest" %% "scalatest" % "3.0.1" % "test" :: Nil

  val breeze: Seq[ModuleID] =
    "org.scalanlp" %% "breeze" % "0.13.2" ::
    "org.scalanlp" %% "breeze-natives" % "0.13.2" :: Nil
}
