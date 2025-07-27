import org.openurp.parent.Dependencies.*
import org.openurp.parent.Settings.*

ThisBuild / organization := "org.openurp.starter"
ThisBuild / version := "0.3.59-SNAPSHOT"

ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/openurp/starter"),
    "scm:git@github.com:openurp/starter.git"
  )
)

ThisBuild / developers := List(
  Developer(
    id = "chaostone",
    name = "Tihua Duan",
    email = "duantihua@gmail.com",
    url = url("http://github.com/duantihua")
  )
)

ThisBuild / description := "OpenURP Starter"
ThisBuild / homepage := Some(url("http://openurp.github.io/starter/index.html"))

val apiVersion = "0.44.1"
val openurp_base_api = "org.openurp.base" % "openurp-base-api" % apiVersion
val ojdbc11 = "com.oracle.database.jdbc" % "ojdbc11" % "23.8.0.25.04"
val orai18n = "com.oracle.database.nls" % "orai18n" % "23.8.0.25.04"

val commonLibs = Seq(beangle_commons, beangle_ems_app, beangle_model, beangle_cdi, beangle_jdbc, logback_classic,
  spring_context, spring_beans, spring_tx, spring_jdbc,
  hibernate_core, hibernate_jcache, caffeine_jcache,
  ojdbc11, orai18n,
  openurp_base_api)

lazy val root = (project in file("."))
  .settings(common)
  .aggregate(web, ws, task)

lazy val web = (project in file("web"))
  .settings(
    name := "openurp-starter-web",
    common,
    libraryDependencies ++= commonLibs,
    libraryDependencies ++= Seq(beangle_bui_bootstrap),
    libraryDependencies ++= Seq(beangle_webmvc, beangle_doc_transfer),
    libraryDependencies ++= Seq(freemarker, beangle_template),
    libraryDependencies ++= Seq(protobuf, beangle_serializer)
  )

lazy val ws = (project in file("ws"))
  .settings(
    name := "openurp-starter-ws",
    common,
    libraryDependencies ++= commonLibs,
    libraryDependencies ++= Seq(beangle_webmvc, beangle_serializer)
  )

lazy val task = (project in file("task"))
  .settings(
    name := "openurp-starter-task",
    common,
    libraryDependencies ++= commonLibs
  )
publish / skip := true
