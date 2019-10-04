import sbt._

object Dependencies {

  object DependencyVersion {
    val breeze = "1.0"
    val cats = "2.0.0"
    val scalaTest = "3.0.8"
    val scalaCollectionCompat = "2.1.2"
  }

  val compileDependencies: Seq[ModuleID] = Seq(
    "org.scalanlp" %% "breeze" % DependencyVersion.breeze,
    "org.typelevel" %% "cats-core" % DependencyVersion.cats,
    "org.scala-lang.modules" %% "scala-collection-compat" % DependencyVersion.scalaCollectionCompat
  )

  val testDependencies: Seq[ModuleID] = Seq(
    "org.scalatest" %% "scalatest" % DependencyVersion.scalaTest
  ).map(_ % "test")

  def settings: Seq[ModuleID] = {
    compileDependencies ++ testDependencies
  }
}
