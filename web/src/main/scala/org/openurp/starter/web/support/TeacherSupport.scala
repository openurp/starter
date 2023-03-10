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
import org.beangle.web.action.context.Params
import org.beangle.web.action.support.{ActionSupport, ServletSupport}
import org.beangle.web.action.view.{PathView, View}
import org.openurp.base.edu.model.Teacher
import org.openurp.base.model.{Project, Semester}
import org.openurp.base.service.{ProjectPropertyService, SemesterService}
import org.openurp.base.std.model.Student
import org.openurp.code.Code
import org.openurp.code.service.CodeService

import java.time.LocalDate
import scala.reflect.ClassTag

abstract class TeacherSupport extends ActionSupport with ServletSupport {

  var entityDao: EntityDao = _
  var codeService: CodeService = _
  var semesterService: SemesterService = _
  var projectPropertyService: ProjectPropertyService = _

  def index(): View = {
    val teacher = getTeacher()
    if (null == teacher) {
      forward("not-teacher")
    } else {
      if (teacher.projects.isEmpty) {
        forward("empty-project")
      } else if (teacher.projects.size == 1) {
        given project: Project = teacher.projects.head

        toProject(teacher)
      } else {
        Params.getId("project", classOf[Int]) match {
          case None =>
            request.setAttribute("defaultProjectId", teacher.projects.head.id)
            forward()
          case Some(pid) =>
            teacher.projects.find(_.id == pid) match
              case Some(p) =>
                given project: Project = p

                toProject(teacher)
              case None => forward()
        }
      }
    }
  }

  protected def projectIndex(teacher: Teacher)(using project: Project): View = {
    null
  }

  protected final def getSemester(): Semester = {
    Params.getId("semester", classOf[Int]) match {
      case None => semesterService.get(getProject(), LocalDate.now)
      case Some(id) => entityDao.get(classOf[Semester], id)
    }
  }

  protected final def getProject(): Project = {
    val project = request.getAttribute("project")
    if (null != project) project.asInstanceOf[Project]
    else
      Params.getId("project", classOf[Int]) match {
        case Some(projectId) =>
          val project = entityDao.get(classOf[Project], projectId)
          request.setAttribute("project", project)
          project
        case None => null
      }
  }

  protected final def getTeacher(): Teacher = {
    val teacher = request.getAttribute("teacher")
    if (null != teacher) teacher.asInstanceOf[Teacher]
    else {
      val teachers = entityDao.findBy(classOf[Teacher], "staff.code" -> Securities.user)
      teachers.foreach { t => request.setAttribute("teacher", t) }
      teachers.headOption.orNull
    }
  }

  private def toProject(teacher: Teacher)(using p: Project): View = {
    request.setAttribute("project", p)
    request.setAttribute("teacher", teacher)
    projectIndex(teacher) match {
      case null => forward("projectIndex")
      case v@PathView(p) => if null == p then forward("projectIndex") else v
      case r: View => r
    }
  }

  def getCodes[T <: Code](clazz: Class[T])(using project: Project): collection.Seq[T] = {
    codeService.get(clazz)
  }

  def getCode[T <: Code](clazz: Class[T], id: Int): T = {
    codeService.get(clazz, id)
  }

  def getProjectProperty[T](name: String, defaultValue: T)(using project: Project): T = {
    projectPropertyService.get(project, name, defaultValue)
  }

}
