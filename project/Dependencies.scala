import sbt._

object Dependencies {

  object DependencyVersion {
    val breeze = "1.0"
    val cats = "2.0.0-M4"
    val scalaCSV = "1.3.6"
    val scalaTest = "3.0.8"
  }

  val compileDependencies: Seq[ModuleID] = Seq(
    "org.scalanlp" %% "breeze" % DependencyVersion.breeze,
    "org.typelevel" %% "cats-core" % DependencyVersion.cats,
    "com.github.tototoshi" %% "scala-csv" % DependencyVersion.scalaCSV
  )

  val testDependencies: Seq[ModuleID] = Seq(
    "org.scalatest" %% "scalatest" % DependencyVersion.scalaTest
  ).map(_ % "test")

  def settings: Seq[ModuleID] = {
    compileDependencies ++ testDependencies
  }
}
