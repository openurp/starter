/*
 * Copyright (C) 2005, The OpenURP Software.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openurp.starter.edu.helper

import org.beangle.commons.lang.Strings
import org.beangle.data.dao.OqlBuilder
import org.beangle.data.model.Entity
import org.beangle.security.Securities
import org.beangle.security.authc.Profile
import org.beangle.web.action.support.ServletSupport
import org.beangle.web.action.annotation.ignore
import org.beangle.webmvc.support.action.EntityAction
import org.openurp.base.edu.model.{Project, Semester}
import org.openurp.base.model.Department
import org.openurp.code.Code

import java.time.LocalDate

trait ProjectSupport extends ServletSupport {
  this: EntityAction[_] =>

  def getCodes[T](clazz: Class[T]): Seq[T] = {
    val query = OqlBuilder.from(clazz, "c")
    if (classOf[Code].isAssignableFrom(clazz)) {
      query.where("c.endOn is null or :now between c.beginOn and c.endOn", LocalDate.now)
    }
    query.cacheable()
    entityDao.search(query)
  }

  def findInSchool[T <: Entity[_]](clazz: Class[T]): Seq[T] = {
    val query = OqlBuilder.from(clazz, "aa")
    query.where("aa.school=:school", getProject.school)
    query.orderBy("code")
    entityDao.search(query)
  }

  def findInProject[T <: Entity[_]](clazz: Class[T], orderBy: String = "code"): Seq[T] = {
    val query = OqlBuilder.from(clazz, "aa")
    query.where("aa.project=:project", getProject)
    query.orderBy(orderBy)
    entityDao.search(query)
  }

  @ignore
  final def getProject: Project = {
    new EmsCookieHelper(entityDao).getProject(request, response)
  }

  def addDepart(query: OqlBuilder[_], departPath: String): Unit = {
    getProfileDepartIds match {
      case None => query.where("1=2")
      case Some(d) =>
        if (d != Profile.AllValue) {
          val departIds = Strings.splitToInt(d)
          if (departPath.endsWith(".id")) {
            query.where(departPath + " in(:profile_depart_ids)", departIds)
          } else {
            query.where(departPath + ".id in(:profile_depart_ids)", departIds)
          }
        }
    }
  }

  private def getProfileDepartIds: Option[String] = {
    new EmsCookieHelper(entityDao).getProfile(request, response) match {
      case None => None
      case Some(p) => p.getProperty("department")
    }
  }

  def getDeparts: List[Department] = {
    val project = getProject
    getProfileDepartIds match {
      case None => List.empty
      case Some(d) =>
        val departs =
          if (d == Profile.AllValue) {
            entityDao.getAll(classOf[Department])
          } else {
            entityDao.find(classOf[Department], Strings.splitToInt(d))
          }
        val pds = project.departments.toSet
        val now = LocalDate.now()
        val rs = departs.filter { d =>
          pds.contains(d) && (d.endOn.isEmpty || !d.endOn.get.isAfter(now))
        }
        rs.toList
    }
  }

  def getCurrentSemester: Semester = {
    val builder = OqlBuilder.from(classOf[Semester], "semester")
      .where("semester.calendar in(:calendars)", getProject.calendars)
    builder.where(":date between semester.beginOn and  semester.endOn", LocalDate.now)
    builder.cacheable()
    val rs = entityDao.search(builder)
    if (rs.isEmpty) { //如果没有正在其中的学期，则查找一个距离最近的
      val builder2 = OqlBuilder.from(classOf[Semester], "semester")
        .where("semester.calendar in(:calendars)", getProject.calendars)
      builder2.orderBy("abs(semester.beginOn - current_date() + semester.endOn - current_date())")
      builder2.cacheable()
      builder2.limit(1, 1)
      entityDao.search(builder2).headOption.orNull
    } else {
      rs.head
    }
  }

  def getUserProjects(clazz: Class[_]): Iterable[Project] = {
    val builder = OqlBuilder.from[Project](clazz.getName, "s")
    builder.where("s.user.code=:code", Securities.user)
    builder.select("s.project")
    builder.orderBy("s.project.code")
    entityDao.search(builder)
  }

  def getUser[A](clazz: Class[A]): A = {
    getInt("project.id") match {
      case None =>
        val builder = OqlBuilder.from(clazz, "s")
        builder.where("s.user.code=:code", Securities.user)
        val stds = entityDao.search(builder)
        if (stds.size == 1) {
          stds.head
        } else {
          throw new RuntimeException("find more than one user for:" + Securities.user + ", project param needed.")
        }
      case Some(projectId) =>
        val builder = OqlBuilder.from(clazz, "s")
        builder.where("s.project.id=:projectId and s.user.code=:code", projectId, Securities.user)
        entityDao.search(builder).head
    }
  }
}
