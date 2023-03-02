package org.needle4k.db.jpa.jakarta

import org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl
import org.hibernate.jpa.boot.internal.PersistenceUnitInfoDescriptor
import org.hibernate.tool.schema.Action.*
import org.needle4k.db.jpa.AbstractEntityManagerFactory
import org.needle4k.configuration.NeedleConfiguration
import java.util.*
import jakarta.persistence.EntityManager
import jakarta.persistence.EntityManagerFactory
import jakarta.persistence.spi.PersistenceUnitInfo

@Suppress("MemberVisibilityCanBePrivate")
class JpaEntityManagerFactory(needleConfiguration: NeedleConfiguration, entityClasses: Collection<Class<*>>) :
  AbstractEntityManagerFactory(needleConfiguration, entityClasses) {
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
}