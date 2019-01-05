name := "akka-persistence-cassandra"

version := "2.4.11"

scalaVersion := "2.11.11"

lazy val akkaVersion = "2.5.3"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-persistence" % akkaVersion,
  "com.typesafe.akka" %% "akka-persistence-cassandra" % "0.54",
  "com.typesafe.akka" %% "akka-persistence-query" % "2.5.3"
)


