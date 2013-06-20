import sbt._
import Keys._

object Tai64Build extends Build {
    lazy val tai64 = Project(id = "tai64", base = file("."))
}

// vim: set ts=4 sw=4 et:
