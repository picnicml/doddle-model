import sbt._

object Dependencies {

  object DependencyVersion {
    val breeze = "0.13.2"
    val cats = "1.6.0"
    val scalaCSV = "1.3.5"
    val scalaTest = "3.0.7"
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
