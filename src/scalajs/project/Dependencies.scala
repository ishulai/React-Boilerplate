import sbt._
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._

object Dependencies {

  //lazy val scalaJSDOM = Def.setting("org.scala-js" %%% "scalajs-dom" % Versions.scalaJS)
  lazy val scalaJSDOM = Def.setting("org.scala-js" %% "scalajs-dom_sjs0.6" % Versions.scalaJS)

  object akka {
    object JVM {
      lazy val actor = Def.setting("com.typesafe.akka" %% "akka-actor" % Versions.akka)
      lazy val remote = Def.setting("com.typesafe.akka" %% "akka-remote" % Versions.akka)
      lazy val stream = Def.setting("com.typesafe.akka" %% "akka-stream" % Versions.akka)
      lazy val http = Def.setting("com.typesafe.akka" %% "akka-http" % Versions.akkaHttp)
      lazy val sprayJson = Def.setting("com.typesafe.akka" %% "akka-http-spray-json" % Versions.akkaHttp)
    }

    object JS {
      lazy val actor = Def.setting("org.akka-js" %%% "akkajsactor" % Versions.akkaJS)
      lazy val stream = Def.setting("org.akka-js" %%% "akkajsactorstream" % Versions.akkaJS)
    }

    // This all value is delicated to JVM only
    import JVM._
    lazy val all = Def.setting(Seq(actor.value, remote.value, stream.value, http.value, sprayJson.value))
  }

  object jackson {
    lazy val databind = Def.setting("com.fasterxml.jackson.core" % "jackson-databind" % Versions.jackson)
    lazy val moduleScala = Def.setting("com.fasterxml.jackson.module" %% "jackson-module-scala" % Versions.jackson)
  }

  object circe {
    object JVM {
      lazy val core = Def.setting("io.circe" %% "circe-core" % Versions.circe)
      lazy val generic = Def.setting("io.circe" %% "circe-generic" % Versions.circe)
      lazy val parser = Def.setting("io.circe" %% "circe-parser" % Versions.circe)
      lazy val all = Def.setting(Seq(core.value, generic.value, parser.value))
    }

    object JS {
      lazy val core = Def.setting("io.circe" %% "circe-scalajs_sjs0.6" % Versions.circe)
      lazy val generic = Def.setting("io.circe" %%% "circe-generic" % Versions.circe)
      lazy val parser = Def.setting("io.circe" %%% "circe-parser" % Versions.circe)
      lazy val all = Def.setting(Seq(core.value, generic.value, parser.value))
    }
  }

  object spark {
    lazy val sql = Def.setting("org.apache.spark" %% "spark-sql" % Versions.spark)
    lazy val ml = Def.setting("org.apache.spark" %% "spark-mllib" % Versions.spark)
    lazy val hive = Def.setting("org.apache.spark" %% "spark-hive" % Versions.spark)

    lazy val all = Def.setting(Seq(sql.value, hive.value))
  }

  object JDBC {
    lazy val mssql = Def.setting("com.microsoft.sqlserver" % "mssql-jdbc" % Versions.jdbc_mssql)
  }

}
