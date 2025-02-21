import org.openurp.parent.Dependencies.*
import org.openurp.parent.Settings.*

ThisBuild / organization := "org.openurp.starter"
ThisBuild / version := "0.3.53-SNAPSHOT"

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

val apiVersion = "0.41.15"
val openurp_base_api = "org.openurp.base" % "openurp-base-api" % apiVersion
val ojdbc11 = "com.oracle.database.jdbc" % "ojdbc11" % "23.6.0.24.10"
val orai18n = "com.oracle.database.nls" % "orai18n" % "23.6.0.24.10"

lazy val root = (project in file("."))
  .settings(common)
  .aggregate(web, ws, task)

lazy val web = (project in file("web"))
  .settings(
    name := "openurp-starter-web",
    common,
    libraryDependencies ++= Seq(beangle_commons, beangle_ems_app, beangle_bui_bootstrap, beangle_serializer),
    libraryDependencies ++= Seq(beangle_model, beangle_cdi, beangle_jdbc, beangle_webmvc, beangle_doc_transfer, beangle_template),
    libraryDependencies ++= Seq(spring_context, spring_beans, spring_tx, spring_jdbc),
    libraryDependencies ++= Seq(freemarker, hibernate_core, hibernate_jcache, caffeine_jcache),
    libraryDependencies ++= Seq(logback_classic, protobuf, openurp_base_api),
    libraryDependencies ++= Seq(ojdbc11, orai18n)
  )

lazy val ws = (project in file("ws"))
  .settings(
    name := "openurp-starter-ws",
    common,
    libraryDependencies ++= Seq(beangle_commons, beangle_ems_app, beangle_webmvc, beangle_serializer),
    libraryDependencies ++= Seq(beangle_model, beangle_cdi, beangle_jdbc),
    libraryDependencies ++= Seq(spring_context, spring_beans, spring_tx, spring_jdbc),
    libraryDependencies ++= Seq(openurp_base_api, hibernate_core, hibernate_jcache, caffeine_jcache),
    libraryDependencies ++= Seq(logback_classic),
    libraryDependencies ++= Seq(ojdbc11, orai18n)
  )

lazy val task = (project in file("task"))
  .settings(
    name := "openurp-starter-task",
    common,
    libraryDependencies ++= Seq(beangle_commons, beangle_ems_app),
    libraryDependencies ++= Seq(beangle_model, beangle_cdi, beangle_jdbc),
    libraryDependencies ++= Seq(spring_context, spring_beans, spring_tx, spring_jdbc),
    libraryDependencies ++= Seq(openurp_base_api, hibernate_core, hibernate_jcache, caffeine_jcache),
    libraryDependencies ++= Seq(logback_classic),
    libraryDependencies ++= Seq(ojdbc11, orai18n)
  )
publish / skip := true
