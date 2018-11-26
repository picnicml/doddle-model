
lazy val root = (project in file("."))
  .settings(
    name := "doddle-model",
    organization := "io.github.picnicml",
    homepage := Some(url("https://picnicml.github.io")),
    scmInfo := Some(ScmInfo(
      url("https://github.com/picnicml/doddle-model"),
      "https://github.com/picnicml/doddle-model.git")
    ),
    version := Version(),
    scalaVersion := "2.12.6",
    crossScalaVersions := Seq("2.11.12", "2.12.7"),
    libraryDependencies ++= Dependencies.settings,
    developers := List(
      Developer("inejc", "Nejc Ilenic", "nejc.ilenic@gmail.com", url("https://github.com/inejc"))
    ),
    licenses := List("MIT" -> url("https://opensource.org/licenses/MIT")),
    publishMavenStyle := true,
    publishTo := Some(
      if (isSnapshot.value)
        Opts.resolver.sonatypeSnapshots
      else
        Opts.resolver.sonatypeStaging
    )
  )

val compilerOptions = Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-feature",
  "-language:existentials",
  "-language:higherKinds",
  "-unchecked",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Xfuture",
  "-Yno-predef",
  "-Ywarn-unused-import"
)
