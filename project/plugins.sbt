
addSbtPlugin("com.typesafe.sbt" % "sbt-digest" % "1.1.0")

// adds the task `dependencyUpdates` which shows a list of project dependencies that can be updated
addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.1.10")

// adds several tasks that show the dependency tree. One of them is `dependencyBrowseGraph`, which opens a browser window with a visualization of the dependency graph
addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.8.2")

// adds the tasks `re-start`, `re-stop`, and  `re-status` 
addSbtPlugin("io.spray" % "sbt-revolver" % "0.8.0")

addSbtPlugin("org.scoverage"          % "sbt-scoverage"                               % "1.5.1")
