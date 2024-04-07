import org.openurp.parent.Dependencies.*
import org.openurp.parent.Settings.*

ThisBuild / organization := "org.openurp.starter"
ThisBuild / version := "0.3.29"

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

val apiVersion = "0.38.0"
val openurp_base_api = "org.openurp.base" % "openurp-base-api" % apiVersion

lazy val root = (project in file("."))
  .settings()
  .aggregate(web, ws)

lazy val web = (project in file("web"))
  .settings(
    name := "openurp-starter-web",
    common,
    libraryDependencies ++= Seq(beangle_commons, beangle_ems_app, beangle_webmvc, beangle_serializer),
    libraryDependencies ++= Seq(beangle_model, beangle_cdi, beangle_doc_transfer),
    libraryDependencies ++= Seq(spring_context, spring_beans, spring_tx, spring_jdbc),
    libraryDependencies ++= Seq(freemarker, hibernate_core, hibernate_jcache, caffeine_jcache),
    libraryDependencies ++= Seq(logback_classic, protobuf, openurp_base_api)
  )

lazy val ws = (project in file("ws"))
  .settings(
    name := "openurp-starter-ws",
    common,
    libraryDependencies ++= Seq(beangle_commons, beangle_ems_app, beangle_webmvc, beangle_serializer),
    libraryDependencies ++= Seq(beangle_model, beangle_cdi),
    libraryDependencies ++= Seq(spring_context, spring_beans, spring_tx, spring_jdbc),
    libraryDependencies ++= Seq(openurp_base_api, hibernate_core, hibernate_jcache, caffeine_jcache),
    libraryDependencies ++= Seq(logback_classic)
  )

publish / skip := true
