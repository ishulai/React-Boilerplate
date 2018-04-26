name := "sparks"
scalaVersion in ThisBuild := Versions.scala
updateOptions := updateOptions.value.withCachedResolution(true)

lazy val controls = project
  .enablePlugins(ScalaJSPlugin)
  .in(file("supplements/controls"))
  .settings(
    name := "controls",
    version := Versions.sparks,
    libraryDependencies ++=
      Dependencies.circe.JS.all.value ++ Seq(Dependencies.scalaJSDOM.value)
  )
  .dependsOn(facades)

lazy val facades = project
  .enablePlugins(ScalaJSPlugin)
  .in(file("supplements/facades"))
  .settings(
    name := "facades",
    version := Versions.sparks,
    libraryDependencies ++= Seq(Dependencies.scalaJSDOM.value)
  )

lazy val dataflow_core = project
  .enablePlugins(ScalaJSPlugin)
  .in(file("dataflow/core"))
  .settings(
    name := "dataflow_core",
    version := Versions.sparks,
    libraryDependencies ++= Seq(
      Dependencies.akka.JVM.sprayJson.value,
      Dependencies.spark.sql.value
    ) ++ Dependencies.circe.JVM.all.value
  )

lazy val connector_base = project
  .enablePlugins(ScalaJSPlugin)
  .in(file("dataflow/connector/base"))
  .settings(
    name := "connector_base",
    version := Versions.sparks,
    libraryDependencies ++= Seq(
      Dependencies.scalaJSDOM.value,
      Dependencies.spark.sql.value
    ) ++ Dependencies.circe.JS.all.value
  )
  .dependsOn(controls, dataflow_core)

lazy val connector_builtin = project
  .enablePlugins(ScalaJSPlugin)
  .in(file("dataflow/connector/builtin"))
  .settings(
    name := "connector_builtin",
    version := Versions.sparks,
    libraryDependencies ++= Seq(Dependencies.scalaJSDOM.value) ++ Dependencies.circe.JS.all.value
  )
  .dependsOn(controls, facades, dataflow_core, connector_base, connector_protocols)

lazy val connector_protocols = project
  .enablePlugins(ScalaJSPlugin)
  .in(file("dataflow/connector/protocols"))
  .settings(
    name := "connector_protocols",
    version := Versions.sparks,
    libraryDependencies ++= Seq(
      Dependencies.spark.sql.value,
      Dependencies.JDBC.mssql.value
    ) ++ Dependencies.circe.JVM.all.value
  )
  .dependsOn(connector_base)

lazy val component_base = project
  .enablePlugins(ScalaJSPlugin)
  .in(file("dataflow/component/base"))
  .settings(
    name := "component_base",
    version := Versions.sparks,
    libraryDependencies ++= Seq(Dependencies.scalaJSDOM.value) ++ Dependencies.circe.JS.all.value
  )
  .dependsOn(controls, facades, dataflow_core, connector_base)

lazy val component_builtin = project
  .enablePlugins(ScalaJSPlugin)
  .in(file("dataflow/component/builtin"))
  .settings(
    name := "component_builtin",
    version := Versions.sparks,
    libraryDependencies ++= Seq(Dependencies.scalaJSDOM.value) ++ Dependencies.circe.JS.all.value
  )
  .dependsOn(controls, facades, dataflow_core, connector_base, component_base, component_protocols)

lazy val component_protocols = project
  .enablePlugins(ScalaJSPlugin)
  .in(file("dataflow/component/protocols"))
  .settings(
    name := "component_protocols",
    version := Versions.sparks,
    libraryDependencies ++= Seq(Dependencies.spark.ml.value) ++ Dependencies.circe.JVM.all.value
  )
  .dependsOn(dataflow_core, connector_base)

lazy val dataflow_designer = project
  .enablePlugins(ScalaJSPlugin)
  .in(file("dataflow/designer"))
  .settings(
    name := "dataflow_designer",
    version := Versions.sparks,
    emitSourceMaps := true,
    relativeSourceMaps := false,
    //scalaJSLinkerConfig ~= { _.withOptimizer(false) },
    mainClass := Some("sparks.dataflow.designer.DataFlowDesigner"),
    libraryDependencies ++= Seq(
      Dependencies.scalaJSDOM.value
    ) ++ Dependencies.circe.JS.all.value
  )
  .dependsOn(controls, facades, dataflow_core, component_base, component_builtin, connector_base, connector_builtin)