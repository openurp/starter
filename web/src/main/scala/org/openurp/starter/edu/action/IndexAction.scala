/*
 * OpenURP, Agile University Resource Planning Solution.
 *
 * Copyright © 2014, The OpenURP Software.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful.
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.openurp.starter.edu.action

import org.beangle.ems.app.web.NavContext
import org.beangle.security.Securities
import org.beangle.security.realm.cas.{Cas, CasConfig}
import org.beangle.security.session.cache.CacheSessionRepo
import org.beangle.webmvc.api.action.{ActionSupport, ServletSupport}
import org.beangle.webmvc.api.annotation.action
import org.beangle.webmvc.api.context.ActionContext
import org.beangle.webmvc.api.view.View
import org.beangle.webmvc.entity.action.EntityAction
import org.openurp.base.edu.model.Project

/**
 * @author xinzhou
 */
@action("")
class IndexAction extends ActionSupport with EntityAction[Project] with ServletSupport {

  var casConfig: CasConfig = _
  var sessionRepo: CacheSessionRepo = _

  def index(): View = {
    put("nav", NavContext.get(request))
    forward()
  }

  def logout(): View = {
    Securities.session foreach { s =>
      sessionRepo.evict(s.id)
    }
    redirect(to(Cas.cleanup(casConfig, ActionContext.current.request, ActionContext.current.response)), null)
  }
}
