package org.needle4k.db.jpa

import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory
import javax.persistence.spi.PersistenceUnitInfo
import org.hibernate.cfg.AvailableSettings
import org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl
import org.hibernate.jpa.boot.internal.PersistenceUnitInfoDescriptor
import org.hibernate.tool.schema.Action.*
import org.needle4k.configuration.NeedleConfiguration
import java.util.*

@Suppress("MemberVisibilityCanBePrivate")
class JpaEntityManagerFactory(private val needleConfiguration: NeedleConfiguration, entityClasses: Collection<Class<*>>) {
  private val entityClasses = getEntityClassNames(entityClasses)
  var debugSQL = false
  private lateinit var entityManagerFactory: EntityManagerFactory

  fun createEntityManager(): EntityManager = createEntityManagerFactory().createEntityManager()

  fun createEntityManagerFactory(): EntityManagerFactory {
    if (!this::entityManagerFactory.isInitialized || !entityManagerFactory.isOpen) {
      val persistenceUnitInfo = getPersistenceUnitInfo(needleConfiguration.persistenceUnitName)
      val builder = EntityManagerFactoryBuilderImpl(PersistenceUnitInfoDescriptor(persistenceUnitInfo), properties)

      entityManagerFactory = builder.build()
    }

    return entityManagerFactory
  }

  fun shutdown() {
    if (this::entityManagerFactory.isInitialized) {
      entityManagerFactory.close()
    }
  }

  fun getPersistenceUnitInfo(name: String): PersistenceUnitInfo = JpaPersistenceUnitInfo(name, entityClasses, properties)

  private val properties: Properties
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