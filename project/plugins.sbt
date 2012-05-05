// Comment to get more information during initialization
logLevel := Level.Info

// The Typesafe repository 
resolvers ++= Seq(
  "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",
  Classpaths.typesafeResolver
)

// Use the Play sbt plugin for Play projects
addSbtPlugin("play" % "sbt-plugin" % "2.0")

addSbtPlugin("com.typesafe.startscript" % "xsbt-start-script-plugin" % "0.5.1")
