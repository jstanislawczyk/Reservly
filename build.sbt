name := """Queserver"""

version := "2.7.x"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.8"

libraryDependencies += guice
libraryDependencies += "com.google.code.gson" % "gson" % "2.8.5"
libraryDependencies += "io.swagger" %% "swagger-play2" % "1.6.0"

libraryDependencies += "com.typesafe.play" %% "play-slick" % "4.0.0"
libraryDependencies += "com.typesafe.play" %% "play-slick-evolutions" % "4.0.0"
libraryDependencies += "org.postgresql" % "postgresql" % "42.2.5"

dependencyOverrides += "com.fasterxml.jackson.core" % "jackson-core" % "2.8.11"
dependencyOverrides += "com.fasterxml.jackson.core" % "jackson-databind" % "2.8.11"

libraryDependencies += specs2 % Test

scalacOptions ++= Seq(
  "-feature",
  "-deprecation",
  "-Xfatal-warnings"
)
