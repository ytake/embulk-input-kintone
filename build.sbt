enablePlugins(ScalafmtPlugin)

name := "embulk-input-kintone2"
version := "0.1"
scalaVersion := "2.13.5"

resolvers ++= Seq(
  Resolver.jcenterRepo,
  Resolver.sonatypeRepo("releases")
)

libraryDependencies ++= Seq(
  "com.kintone" % "kintone-java-client" % "1.0.4",
  "com.google.inject" % "guice" % "4.2.3" % "provided",
  "com.google.code.gson" % "gson" % "2.8.6",
  "org.embulk" % "embulk-core" % "0.9.7" % "provided",
  "junit" % "junit" % "4.13" % Test,
  "org.assertj" % "assertj-core" % "3.19.0" % Test,
  "org.scalatest" %% "scalatest" % "3.2.7" % Test,
  "org.scalatestplus" %% "junit-4-13" % "3.2.7.0" % Test
)
