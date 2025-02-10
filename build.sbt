ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.4.1"

lazy val root = (project in file("."))
  .settings(
    name := "oystr-hotel",
    scalacOptions += "-Xfatal-warnings",
    libraryDependencies += "org.typelevel" %% "cats-core" % "2.13.0",
    libraryDependencies += "org.typelevel" %% "cats-effect" % "3.5.7",
    libraryDependencies += "org.typelevel" %% "log4cats-slf4j" % "2.7.0",
    libraryDependencies ++= Seq("org.http4s" %% "http4s-core" % "1.0.0-M44",
      "org.http4s" %% "http4s-dsl" % "1.0.0-M44",
      "org.http4s" %% "http4s-ember-server" % "1.0.0-M44",
      "org.http4s" %% "http4s-circe" % "1.0.0-M44"),
    libraryDependencies ++= Seq("io.circe" %% "circe-core" % "0.14.10",
      "io.circe" %% "circe-generic" % "0.14.10"),
    libraryDependencies += "org.tpolecat" %% "skunk-core" % "0.6.4",
    libraryDependencies += "org.scalamock" %% "scalamock-cats-effect" % "7.2.0" % Test,
    libraryDependencies += "com.disneystreaming" %% "weaver-cats" % "0.8.4" % Test,
    testFrameworks += new TestFramework("weaver.framework.CatsEffect"),
    Test / scalacOptions += "-experimental"
)
