package org.needle4k.db.jpa.javax

import jakarta.persistence.EntityManager
import jakarta.persistence.spi.PersistenceUnitInfo
import org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl
import org.hibernate.jpa.boot.internal.PersistenceUnitInfoDescriptor
import org.hibernate.tool.schema.Action.*
import org.needle4k.db.jpa.AbstractEntityManagerFactory
import org.needle4k.configuration.NeedleConfiguration
import java.util.*
import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory
import javax.persistence.spi.PersistenceUnitInfo

@Suppress("MemberVisibilityCanBePrivate")
class JpaEntityManagerFactory(needleConfiguration: NeedleConfiguration, entityClasses: Collection<Class<*>>) :
  AbstractEntityManagerFactory(needleConfiguration, entityClasses) {
  private lateinit var entityManagerFactory: EntityManagerFactory

  fun createEntityManager(): EntityManager = createEntityManagerFactory().createEntityManager()

  fun createEntityManagerFactory(): EntityManagerFactory {
    if (entityManagerFactory == null || !entityManagerFactory.isOpen) {
      val persistenceUnitInfo = getPersistenceUnitInfo(needleConfiguration.persistenceUnitName)
      val builder = EntityManagerFactoryBuilderImpl(PersistenceUnitInfoDescriptor(persistenceUnitInfo), properties)
      entityManagerFactory = builder.build()
    }

    return entityManagerFactory
  }

  fun shutdown() {
    if (entityManagerFactory != null) {
      entityManagerFactory.close()
    }
  }

  fun getPersistenceUnitInfo(name: String): PersistenceUnitInfo = JpaPersistenceUnitInfo(name, entityClasses, properties)
}