package org.needle4k.db.jpa

import jakarta.persistence.SharedCacheMode
import jakarta.persistence.ValidationMode
import jakarta.persistence.spi.ClassTransformer
import jakarta.persistence.spi.PersistenceUnitInfo
import jakarta.persistence.spi.PersistenceUnitTransactionType
import org.hibernate.jpa.HibernatePersistenceProvider
import java.net.URL
import java.util.*
import javax.sql.DataSource

class JpaPersistenceUnitInfo(
  private val persistenceUnitName: String,
  private val managedClassNames: Set<String>,
  private val properties: Properties
) : PersistenceUnitInfo {
  override fun getPersistenceUnitName(): String {
    return persistenceUnitName
  }

  override fun getPersistenceProviderClassName(): String {
    return HibernatePersistenceProvider::class.java.name
  }

  override fun getJtaDataSource(): DataSource {
    throw UnsupportedOperationException()
  }

  override fun getNonJtaDataSource(): DataSource {
    throw UnsupportedOperationException()
  }

  override fun getMappingFileNames(): List<String> {
    return emptyList()
  }

  override fun getJarFileUrls(): List<URL> {
    return emptyList()
  }

  override fun getPersistenceUnitRootUrl(): URL {
    throw UnsupportedOperationException()
  }

  override fun getManagedClassNames(): List<String> {
    return managedClassNames.toList()
  }

  override fun excludeUnlistedClasses(): Boolean {
    return true
  }

  override fun getProperties(): Properties {
    return properties
  }

  override fun getPersistenceXMLSchemaVersion(): String {
    return JPA_VERSION
  }

  override fun getClassLoader(): ClassLoader {
    return ClassLoader.getSystemClassLoader()
  }

  override fun getNewTempClassLoader(): ClassLoader {
    return classLoader
  }

  override fun getTransactionType() = PersistenceUnitTransactionType.RESOURCE_LOCAL

  override fun getSharedCacheMode() = SharedCacheMode.DISABLE_SELECTIVE

  override fun getValidationMode() = ValidationMode.AUTO

  override fun addTransformer(transformer: ClassTransformer) {
    throw UnsupportedOperationException()
  }

  companion object {
    const val JPA_VERSION = "2.1"
  }
}