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
import org.openurp.base.model.{Project, Semester, User}
import org.openurp.base.service.{Feature, ProjectConfigService, SemesterService}
import org.openurp.base.std.model.Student
import org.openurp.code.Code
import org.openurp.code.service.CodeService

abstract class StudentSupport extends ActionSupport, ServletSupport {

  var entityDao: EntityDao = _

  var semesterService: SemesterService = _

  var codeService: CodeService = _

  var configService: ProjectConfigService = _

  final def index(): View = {
    val stds = entityDao.findBy(classOf[Student], "code" -> Securities.user)
    if stds.isEmpty then
      forward("not-student")
    else
      val projects = stds.map(_.project).sortBy(_.code)
      val projectId = getInt("projectId", projects.head.id)
      val student = stds.find(_.project.id == projectId).getOrElse(stds.head)
      put("projects", projects)
      put("student", student)
      put("project", student.project)
      projectIndex(student)
  }

  protected def projectIndex(student: Student): View = {
    forward()
  }

  protected final def getSemester: Semester = {
    SemesterHelper.getSemester(entityDao, semesterService, getProject, request, response)
  }

  protected final def getProject: Project = {
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

  protected final def getStudent: Student = {
    val std = request.getAttribute("student")
    if (null != std) std.asInstanceOf[Student]
    else
      val project = getProject
      if project == null then
        updateRequest(entityDao.findBy(classOf[Student], "code" -> Securities.user).headOption)
      else
        updateRequest(entityDao.findBy(classOf[Student], "project" -> project, "code" -> Securities.user).headOption)
  }

  protected final def getUser: User = {
    entityDao.findBy(classOf[User], "code" -> Securities.user).head
  }

  private def updateRequest(stds: Option[Student]): Student = {
    stds match {
      case Some(std) =>
        request.setAttribute("student", std)
        request.setAttribute("project", std.project)
        std
      case None => null
    }
  }

  def getCodes[T <: Code](clazz: Class[T])(using project: Project): collection.Seq[T] = {
    codeService.get(clazz)
  }

  def getCode[T <: Code](clazz: Class[T], id: Int): T = {
    codeService.get(clazz, id)
  }

  protected def getConfig[T](name: String, defaultValue: T)(using project: Project): T = {
    configService.get(project, name, defaultValue)
  }

  protected def getConfig(f: Feature)(using project: Project): Any = {
    configService.get[Any](project, f)
  }
}
