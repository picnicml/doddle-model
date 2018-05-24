import sbt._

object Dependencies {

  lazy val breezeVersion = "0.13.2"

  val testingDeps: Seq[ModuleID] =
    "org.scalatest" %% "scalatest" % "3.0.1" % "test" :: Nil

  val breeze: Seq[ModuleID] =
    "org.scalanlp" %% "breeze" % breezeVersion ::
    "org.scalanlp" %% "breeze-natives" % breezeVersion :: Nil

  val csv: Seq[ModuleID] =
    "com.github.tototoshi" %% "scala-csv" % "1.3.5" :: Nil
}
