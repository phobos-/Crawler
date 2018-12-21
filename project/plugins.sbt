//static code analysis
addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "1.0.0")
addSbtPlugin("org.wartremover" % "sbt-wartremover" % "2.2.1")

//code formatting
addSbtPlugin("com.lucidchart" % "sbt-scalafmt-coursier" % "1.12")

//test coverage
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.5.1")