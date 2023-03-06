package org.needle4k.db.jpa

import javax.persistence.SharedCacheMode
import javax.persistence.ValidationMode
import javax.persistence.spi.ClassTransformer
import javax.persistence.spi.PersistenceUnitInfo
import javax.persistence.spi.PersistenceUnitTransactionType
import org.hibernate.jpa.HibernatePersistenceProvider
import java.net.URL
import java.util.*
import javax.sql.DataSource

class JpaPersistenceUnitInfo(
  private val persistenceUnitName: String,
  private val managedClassNames: Set<String>,
  private val properties: Properties
) : PersistenceUnitInfo {
  override fun getPersistenceUnitName() = persistenceUnitName

  override fun getPersistenceProviderClassName() = HibernatePersistenceProvider::class.java.name

  override fun getJtaDataSource(): DataSource {
    throw UnsupportedOperationException()
  }

  override fun getNonJtaDataSource(): DataSource {
    throw UnsupportedOperationException()
  }

  override fun getMappingFileNames() = emptyList<String>()

  override fun getJarFileUrls() = emptyList<URL>()

  override fun getPersistenceUnitRootUrl(): URL {
    throw UnsupportedOperationException()
  }

  override fun getManagedClassNames() = managedClassNames.toList()

  override fun excludeUnlistedClasses() = true

  override fun getProperties() = properties

  override fun getPersistenceXMLSchemaVersion() = "2.1"

  override fun getClassLoader() = ClassLoader.getSystemClassLoader()

  override fun getNewTempClassLoader() = classLoader

  override fun getTransactionType() = PersistenceUnitTransactionType.RESOURCE_LOCAL

  override fun getSharedCacheMode() = SharedCacheMode.DISABLE_SELECTIVE

  override fun getValidationMode() = ValidationMode.AUTO

  override fun addTransformer(transformer: ClassTransformer) {
    throw UnsupportedOperationException()
  }
}