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
package org.openurp.starter.web

import jakarta.servlet.ServletContext
import org.beangle.commons.text.inflector.en.EnNounPluralizer
import org.beangle.ems.app.{Ems, EmsApp}

class Initializer extends org.beangle.commons.web.init.Initializer {

  override def onConfig(sc: ServletContext): Unit = {
    System.setProperty("beangle.cdi.reconfig_url",s"${Ems.api}/platform/config/files/${EmsApp.name}/spring-config.xml")
  }

  override def onStartup(sc: ServletContext): Unit = {}
}
