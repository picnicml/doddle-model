lazy val root = (project in file("."))
  .settings(
    name := "doddle-model",
    organization := "com.picnicml",
    version := Version(),
    scalaVersion := "2.12.6",
    crossScalaVersions := Seq("2.11.12", "2.12.7"),
    libraryDependencies ++= Dependencies.settings
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
