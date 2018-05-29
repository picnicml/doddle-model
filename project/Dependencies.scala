import sbt._

object Dependencies {

  object V {
    val breezeVersion = "0.13.2"
    val scalaCSVVersion = "1.3.5"
    val scalaTestVersion = "3.0.1"
  }

  val compileDependencies: Seq[ModuleID] = Seq(
    "org.scalanlp" %% "breeze" % V.breezeVersion,
    "org.scalanlp" %% "breeze-natives" % V.breezeVersion,
    "com.github.tototoshi" %% "scala-csv" % V.scalaCSVVersion
  )

  val testDependencies: Seq[ModuleID] = Seq(
    "org.scalatest" %% "scalatest" % V.scalaTestVersion
  ).map(_ % "test")

  def settings: Seq[ModuleID] = {
    compileDependencies ++ testDependencies
  }
}
