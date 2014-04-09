
import sbt._
import sbt.Keys._

object Dependencies
{
  val akkaDep = Seq (
    "com.typesafe.akka"            %% "akka-actor"                  % "2.3.0",
    "com.typesafe.akka"            %% "akka-slf4j"                  % "2.3.0"
  )

  val logDep     = Seq (
    "com.typesafe"                 %% "scalalogging-slf4j"          % "1.0.1"
  )

}

object AkkaComputeBuild extends Build{

  import Dependencies._

  lazy val akkaExample = Project(id = "akkaComputeExample", base = file("akkaComputeExample"))
    .settings(libraryDependencies ++= akkaDep)

}
