import sbt._
import Keys._

// Shell prompt which shows the current project, git branch and build version
object GitPrompt {
  object devnull extends ProcessLogger {
    def info (s: => String) {}
    def error (s: => String) {}
    def buffer[T] (f: => T): T = f
  }

  val current = """\*\s+([\w-]+)""".r

  def gitBranches = ("git branch --no-color" lines_! devnull mkString)

  val buildShellPrompt = (state: State) => {
    val currBranch =
      current.findFirstMatchIn(gitBranches).map(_.group(1)).getOrElse("-")
    val currProject = Project.extract(state)
    "%s:%s:%s> ".format(currProject.currentProject.id
                      , currBranch
                      , currProject.get(version))
  }
}
