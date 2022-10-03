/*
 * Copyright (C) 2014, The OpenURP Software.
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

package org.openurp.starter.web.support

import org.beangle.data.dao.EntityDao
import org.beangle.security.Securities
import org.beangle.web.action.support.{ActionSupport, ServletSupport}
import org.beangle.web.action.view.View
import org.openurp.base.model.{Project, Semester}
import org.openurp.base.service.SemesterService
import org.openurp.base.std.model.Student

import java.time.LocalDate

trait StdProjectSupport extends ActionSupport with ServletSupport {

  def entityDao: EntityDao

  def semesterService:SemesterService

  def index(): View = {
    val stds = entityDao.findBy(classOf[Student], "code" -> Securities.user)
    if stds.isEmpty then forward("not-student")
    else if stds.size == 1 then
      toProject(stds.head)
    else
      getInt("projectId") match {
        case None =>
          val projects = stds.map(_.project)
          put("projects", projects)
          request.setAttribute("defaultProjectId", projects.head.id)
          forward()
        case Some(pid) =>
          stds.find(_.project.id == pid) match
            case Some(s) => toProject(s)
            case None => forward()
      }
  }

  protected def projectIndex(student: Student): Unit

  protected final def getSemester(): Semester = {
    getInt("semester.id") match {
      case None => semesterService.get(getProject(), LocalDate.now)
      case Some(id) => entityDao.get(classOf[Semester], id)
    }
  }

  protected final def getProject(): Project = {
    val project = request.getAttribute("project")
    if (null != project) project.asInstanceOf[Project]
    else
      getInt("projectId") match {
        case Some(projectId) =>
          val project = entityDao.get(classOf[Project], projectId)
          request.setAttribute("project", project)
          project
        case None => null
      }
  }

  protected final def getStudent(): Student = {
    val std = request.getAttribute("student")
    if (null != std) std.asInstanceOf[Student]
    else
      val project = getProject()
      val stds = entityDao.findBy(classOf[Student], "project" -> project, "code" -> Securities.user)
      stds.foreach { std => request.setAttribute("std", std) }
      stds.head
  }

  private def toProject(student: Student): View = {
    request.setAttribute("student", student)
    request.setAttribute("project", student.project)
    projectIndex(student)
    forward("projectIndex")
  }

}
