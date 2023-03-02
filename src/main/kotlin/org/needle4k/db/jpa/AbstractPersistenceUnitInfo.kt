package org.needle4k.db.jpa

import org.hibernate.jpa.HibernatePersistenceProvider
import java.net.URL
import java.util.*
import javax.sql.DataSource

abstract class AbstractPersistenceUnitInfo(
  private val persistenceUnitName: String,
  private val managedClassNames: Set<String>,
  private val properties: Properties
)  {
  fun getPersistenceUnitName(): String {
    return persistenceUnitName
  }

  fun getPersistenceProviderClassName(): String {
    return HibernatePersistenceProvider::class.java.name
  }

  fun getJtaDataSource(): DataSource {
    throw UnsupportedOperationException()
  }

  fun getNonJtaDataSource(): DataSource {
    throw UnsupportedOperationException()
  }

  fun getMappingFileNames(): List<String> {
    return emptyList()
  }

  fun getJarFileUrls(): List<URL> {
    return emptyList()
  }

  fun getPersistenceUnitRootUrl(): URL {
    throw UnsupportedOperationException()
  }

  fun getManagedClassNames(): List<String> {
    return managedClassNames.toList()
  }

  fun excludeUnlistedClasses(): Boolean {
    return true
  }

  fun getProperties(): Properties {
    return properties
  }

  fun getPersistenceXMLSchemaVersion(): String {
    return JPA_VERSION
  }

  fun getClassLoader(): ClassLoader {
    return ClassLoader.getSystemClassLoader()
  }

  fun getNewTempClassLoader(): ClassLoader {
    return getClassLoader()
  }

  companion object {
    const val JPA_VERSION = "2.1"
  }
}