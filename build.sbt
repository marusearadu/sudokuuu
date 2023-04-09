ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.2.2"

lazy val root = (project in file("."))
  .settings(
    name := "sudokuuu"
  )

libraryDependencies += "org.scalafx" % "scalafx_3" % "19.0.0-R30"
libraryDependencies ++= {

  // Determine OS version of JavaFX binaries
  lazy val osName = System.getProperty("os.name") match {
    case n if n.startsWith("Linux") => "linux"
    case n if n.startsWith("Mac") => "mac"
    case n if n.startsWith("Windows") => "win"
    case _ => throw new Exception("Unknown platform!")
  }
  Seq("base", "controls", "fxml", "graphics", "media", "swing", "web")
    .map(m => "org.openjfx" % s"javafx-$m" % "19" classifier osName)
}
libraryDependencies += "io.spray" %%  "spray-json" % "1.3.6"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.15" % "test"

libraryDependencies += "org.testfx" % "testfx-junit5" % "4.0.16-alpha" % Test

//Junit
libraryDependencies += "org.junit.jupiter" % "junit-jupiter-api" % "5.9.2" % Test
libraryDependencies += "org.junit.jupiter" % "junit-jupiter-engine" % "5.9.2" % Test
libraryDependencies += "org.junit.platform" % "junit-platform-runner" % "1.9.2" % Test

//Junit5 interface, that allows running Junit tests from Scala
//This requires a plugin in project/plugins.sbt
resolvers in ThisBuild += Resolver.jcenterRepo
libraryDependencies ++= Seq(
  "net.aichler" % "jupiter-interface" % JupiterKeys.jupiterVersion.value % Test
)