
name := """Accounts"""
version := "0.1.0-SNAPSHOT"
lazy val Accounts = (project in file(".")).enablePlugins(SbtWeb)

scalaVersion := "2.12.4"
incOptions := incOptions.value.withNameHashing(true)
updateOptions := updateOptions.value.withCachedResolution(cachedResoluton = true)

coverageMinimum := 50
coverageFailOnMinimum := true

lazy val akkaVersion = "2.5.12"

libraryDependencies ++= Seq(
  // binding logback as the underlying logging framework for SLF4J
  "ch.qos.logback" % "logback-classic" % "1.1.7",
  "org.scalatest"                   %% "scalatest"                      % "3.2.0-SNAP10"        % "test",
  "com.typesafe.akka"                  %% "akka-testkit"                   % akkaVersion             % "test",
  "com.typesafe.akka"                  %% "akka-http-testkit"                   % "10.1.1"             % "test",
  "org.scalacheck"                  %% "scalacheck"                     % "1.14.0",
  "org.mockito"                      % "mockito-core"                   % "2.13.0"                        % "test",
  "org.scalamock"                   %% "scalamock-scalatest-support"    % "3.6.0"                         % "test",
  "org.scoverage"                   %% "scalac-scoverage-runtime"       % "1.4.0-M3"                      % "test",

//akka
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-http" % "10.1.1",
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,

  "org.json4s"                      %% "json4s-native"                  % "3.6.0-M2",
  "com.google.code.gson"             % "gson"                            % "2.8.2",
  "com.typesafe"                     % "config"                         % "1.3.2",
  "org.apache.httpcomponents"        % "httpclient"                     % "4.5.4"
)
