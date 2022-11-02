name := "LogAnalyzer-grpc-rest-api"

version := "0.1"

scalaVersion := "3.2.0"

val logbackVersion        = "1.4.4"
val sfl4sVersion          = "2.0.3"
val typesafeConfigVersion = "1.4.2"
val apacheCommonIOVersion = "2.11.0"
val scalacticVersion      = "3.2.14"
val generexVersion        = "1.0.2"
val scalapbVersion        = "1.0.0"

resolvers += Resolver.jcenterRepo

lazy val root = (project in file("."))
  .settings(
    name             := "LogAnalyzer-grpc-rest-api",
    idePackagePrefix := Some("vvakic2.uic.cs441")
  )

Compile / PB.targets := Seq(
  scalapb.gen() -> (Compile / sourceManaged).value
)

libraryDependencies ++= Seq(
  "com.typesafe"          % "config"               % typesafeConfigVersion,
  "io.grpc"               % "grpc-netty"           % scalapb.compiler.Version.grpcJavaVersion,
  "com.thesamet.scalapb" %% "scalapb-runtime-grpc" % scalapb.compiler.Version.scalapbVersion,
  "com.thesamet.scalapb" %% "scalapb-runtime" % scalapb.compiler.Version.scalapbVersion % "protobuf"
)

libraryDependencies += "org.apache.httpcomponents" % "httpclient"   % "4.5.13"
libraryDependencies += "org.scalatestplus"        %% "mockito-4-6"  % "3.2.14.0" % "test"
libraryDependencies += "org.mockito"               % "mockito-core" % "4.8.0"    % Test

libraryDependencies ++= Seq(
  "ch.qos.logback" % "logback-core"          % logbackVersion,
  "ch.qos.logback" % "logback-classic"       % logbackVersion,
  "org.slf4j"      % "slf4j-api"             % sfl4sVersion,
  "org.scalactic" %% "scalactic"             % scalacticVersion,
  "org.scalatest" %% "scalatest"             % scalacticVersion % Test,
  "org.scalatest" %% "scalatest-featurespec" % scalacticVersion % Test,
  "com.typesafe"   % "config"                % typesafeConfigVersion
)
