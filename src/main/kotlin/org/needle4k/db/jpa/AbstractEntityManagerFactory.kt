package org.needle4k.db.jpa

import org.hibernate.cfg.AvailableSettings
import org.hibernate.tool.schema.Action.*
import org.needle4k.configuration.NeedleConfiguration
import java.util.*

@Suppress("MemberVisibilityCanBePrivate")
abstract class AbstractEntityManagerFactory(protected val needleConfiguration: NeedleConfiguration, entityClasses: Collection<Class<*>>) {
  protected val entityClasses = getEntityClassNames(entityClasses)
  var debugSQL = false

  protected val properties: Properties
    get() {
      val properties = Properties()
      val type = needleConfiguration.jdbcConfiguration.databaseType

      properties[AvailableSettings.DIALECT] = type.latestDialect().name
      properties[AvailableSettings.DRIVER] = needleConfiguration.jdbcConfiguration.jdbcDriver
      properties[AvailableSettings.HBM2DDL_AUTO] = CREATE_DROP.name
      properties[AvailableSettings.URL] = needleConfiguration.jdbcConfiguration.jdbcUrl
      properties[AvailableSettings.SHOW_SQL] = debugSQL.toString()
      properties[AvailableSettings.FORMAT_SQL] = "true"
      properties[AvailableSettings.USE_SECOND_LEVEL_CACHE] = "false"
      properties[AvailableSettings.USE_QUERY_CACHE] = "false"

      return properties
    }

  companion object {
    private fun getEntityClassNames(entityClasses: Collection<Class<*>>) = entityClasses.map { it.name }.toMutableSet()
  }
}