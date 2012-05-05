import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {
  val appName = "play20-mongodb-sample"
  val appVersion = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    "com.novus" %% "salat-core" % "0.0.8-SNAPSHOT",
    "io.backchat.inflector" %% "scala-inflector" % "1.3.3"
  )

  val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
    resolvers += "Novus Release Repository" at "http://repo.novus.com/releases/",
    resolvers += "Novus Snapshots Repository" at "http://repo.novus.com/snapshots/"
  )
}
