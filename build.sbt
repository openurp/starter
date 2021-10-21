import org.openurp.parent.Settings._
import org.openurp.parent.Dependencies._

ThisBuild / organization := "org.openurp.starter"
ThisBuild / version := "0.0.11-SNAPSHOT"

ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/openurp/starter"),
    "scm:git@github.com:openurp/starter.git"
  )
)

ThisBuild / developers := List(
  Developer(
    id    = "chaostone",
    name  = "Tihua Duan",
    email = "duantihua@gmail.com",
    url   = url("http://github.com/duantihua")
  )
)

ThisBuild / description := "OpenURP Starter"
ThisBuild / homepage := Some(url("http://openurp.github.io/starter/index.html"))

val apiVersion="0.23.2"
val openurp_base_api ="org.openurp.base" % "openurp-base-api" %apiVersion

lazy val root = (project in file("."))
  .settings()
  .aggregate(web,ws)

lazy val web = (project in file("web"))
  .settings(
    name := "openurp-starter-web",
    common,
    libraryDependencies ++= Seq(beangle_ems_app,beangle_webmvc_bootstrap,beangle_webmvc_support,beangle_data_transfer),
    libraryDependencies ++= Seq(openurp_base_api,beangle_data_hibernate,hibernate_jcache,ehcache,beangle_cdi_api),
    libraryDependencies ++= Seq(beangle_webmvc_freemarker),
  )

lazy val ws = (project in file("ws"))
  .settings(
    name := "openurp-starter-ws",
    common,
    libraryDependencies ++= Seq(beangle_ems_app,beangle_webmvc_support,beangle_serializer_text),
    libraryDependencies ++= Seq(openurp_base_api,beangle_data_hibernate,hibernate_jcache,ehcache,beangle_cdi_api)
  )

publish / skip := true
