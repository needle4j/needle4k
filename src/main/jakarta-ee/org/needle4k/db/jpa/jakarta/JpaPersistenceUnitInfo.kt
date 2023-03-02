package org.needle4k.db.jpa.jakarta

import java.util.*
import jakarta.persistence.SharedCacheMode
import jakarta.persistence.ValidationMode
import jakarta.persistence.spi.ClassTransformer
import jakarta.persistence.spi.PersistenceUnitInfo
import jakarta.persistence.spi.PersistenceUnitTransactionType
import org.needle4k.db.jpa.AbstractPersistenceUnitInfo

internal class JpaPersistenceUnitInfo(
  persistenceUnitName: String,
  managedClassNames: Set<String>,
  properties: Properties
) : AbstractPersistenceUnitInfo(persistenceUnitName, managedClassNames, properties), PersistenceUnitInfo {
  override fun getTransactionType() = PersistenceUnitTransactionType.RESOURCE_LOCAL

  override fun getSharedCacheMode() = SharedCacheMode.DISABLE_SELECTIVE

  override fun getValidationMode() = ValidationMode.AUTO

  override fun addTransformer(transformer: ClassTransformer) {
    throw UnsupportedOperationException()
  }
}