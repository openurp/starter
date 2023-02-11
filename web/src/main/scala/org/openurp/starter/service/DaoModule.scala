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

package org.openurp.starter.service

import org.beangle.cache.concurrent.ConcurrentMapCacheManager
import org.beangle.cdi.bind.BindModule
import org.beangle.commons.lang.ClassLoaders
import org.beangle.webmvc.hibernate.CloseSessionInterceptor
import org.beangle.data.orm.hibernate.{HibernateTransactionManager, LocalSessionFactoryBean}
import org.beangle.data.orm.hibernate.{DomainFactory, HibernateEntityDao}
import org.beangle.ems.app.datasource.AppDataSourceFactory
import org.springframework.beans.factory.config.PropertiesFactoryBean
import org.springframework.transaction.interceptor.TransactionProxyFactoryBean

object DaoModule extends BindModule {

  protected override def binding(): Unit = {
    bind(classOf[AppDataSourceFactory])

    bind("SessionFactory.default", classOf[LocalSessionFactoryBean])
      .property("devMode",devEnabled)
      .property("ormLocations", "classpath*:META-INF/beangle/orm.xml").primary()

    bind("HibernateTransactionManager.default", classOf[HibernateTransactionManager]).primary()

    bind("TransactionProxy.template", classOf[TransactionProxyFactoryBean]).setAbstract().property(
      "transactionAttributes",
      props("save*=PROPAGATION_REQUIRED", "update*=PROPAGATION_REQUIRED", "delete*=PROPAGATION_REQUIRED",
        "batch*=PROPAGATION_REQUIRED", "execute*=PROPAGATION_REQUIRED", "remove*=PROPAGATION_REQUIRED",
        "*=PROPAGATION_REQUIRED,readOnly")).primary()

    bind(classOf[DomainFactory]).constructor(list(ref("SessionFactory.default")))

    bind("EntityDao.hibernate", classOf[TransactionProxyFactoryBean]).proxy("target", classOf[HibernateEntityDao])
      .parent("TransactionProxy.template").primary().description("基于Hibernate提供的通用DAO")

    bind("web.Interceptor.hibernate", classOf[CloseSessionInterceptor])

    bind("CacheManager.concurrent", classOf[ConcurrentMapCacheManager])
  }

}
