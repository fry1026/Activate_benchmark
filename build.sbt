name := "Activate_benchmark"

version := "1.0"

scalaVersion := "2.11.5"

libraryDependencies ++= {
  Seq(
    "net.fwbrasil" %% "activate-core" % "1.7" exclude("org.scala-stm", "scala-stm_2.11.0"),
    "net.fwbrasil" %% "activate-jdbc" % "1.7" exclude("org.scala-stm", "scala-stm_2.11.0"),
    "net.fwbrasil" %% "activate-jdbc-async" % "1.7" exclude("org.scala-stm", "scala-stm_2.11.0"),
    "org.postgresql" % "postgresql" % "9.3-1101-jdbc41",
    "com.h2database" % "h2" % "1.2.127"
  )
}