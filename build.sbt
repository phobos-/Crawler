name := "crawler"

version := "0.1"

scalaVersion := "2.12.8"

lazy val crawler = project
  .in(file("."))
  .settings(
    commonSettings,
    libraryDependencies ++= Seq(
      dependencies.typesafeConfig,
      dependencies.scalatest % "test",
      dependencies.scalacheck % "test"
    )
  )

lazy val dependencies =
  new {
    val scalaFmtV = "1.2.0"

    val scalatestV = "3.0.4"
    val scalacheckV = "1.13.5"
    val typesafeCfgV = "1.3.3"

    val scalatest = "org.scalatest" %% "scalatest" % scalatestV
    val scalacheck = "org.scalacheck" %% "scalacheck" % scalacheckV
    val typesafeConfig = "com.typesafe" % "config" % typesafeCfgV
  }

lazy val compilerOptions = Seq(
  "-unchecked",
  "-feature",
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-language:postfixOps",
  "-deprecation",
  "-encoding",
  "utf8"
)

lazy val notNeededWarts = Seq(
  Wart.Throw,
  Wart.ImplicitParameter,
  Wart.ToString,
  Wart.NonUnitStatements,
  Wart.StringPlusAny,
  Wart.Any,
  Wart.AsInstanceOf,
  Wart.Product,
  Wart.Serializable,
  Wart.Var,
  Wart.DefaultArguments,
  Wart.While,
  Wart.Nothing,
  Wart.Overloading
)

lazy val commonSettings = Seq(
  scalacOptions ++= compilerOptions,
  wartremoverWarnings in (Compile, compile) ++= Warts.allBut(
    notNeededWarts: _*),
  scalastyleConfig := file("project/scalastyle-config.xml"),
  scalafmtOnCompile := true,
  scalafmtTestOnCompile := true,
  scalafmtVersion := dependencies.scalaFmtV,
  coverageEnabled := true
)
