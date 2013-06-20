name := "tai64"

organization := "com.soundcloud"

version := "1.0.0-SNAPSHOT"

shellPrompt := GitPrompt.buildShellPrompt

scalaVersion := "2.10.2"

scalacOptions ++= Seq("-deprecation", "-unchecked", "-optimize")

libraryDependencies <++= (scalaVersion) { v =>
  Seq(
    "joda-time" % "joda-time" % "2.2"
  , "org.joda" % "joda-convert" % "1.3.+"
  , "org.scala-lang" % "scala-reflect" % v
  , "org.scalacheck" %% "scalacheck" % "1.10.+" % "test"
  )
}

publishArtifact in (Compile, packageDoc) := false

publishArtifact in (Compile, packageSrc) := false

publishTo <<= version { (v: String) =>
  val sc = "http://maven.int.s-cloud.net/content/repositories/"
  if (v.trim.endsWith("SNAPSHOT"))
    Some("snapshots" at sc + "snapshots")
  else
    Some("releases"  at sc + "releases")
}
