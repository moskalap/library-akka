name := "bookstore-akka"

version := "0.1"

scalaVersion := "2.12.6"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.5.12",
  "com.typesafe.akka" %% "akka-testkit" % "2.5.12" % Test,
  "com.typesafe.akka" %% "akka-stream" % "2.5.12",
  "com.typesafe.akka" %% "akka-stream-testkit" % "2.5.12" % Test,
  "com.typesafe.akka" %% "akka-remote" % "2.5.12",
  "com.typesafe.akka" %% "akka-slf4j" % "2.5.12",
  "ch.qos.logback" % "logback-classic" % "1.0.9"

)