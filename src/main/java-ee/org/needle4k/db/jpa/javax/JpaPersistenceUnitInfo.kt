package org.needle4k.db.jpa.javax

import org.needle4k.db.jpa.AbstractPersistenceUnitInfo
import java.util.*
import javax.persistence.SharedCacheMode
import javax.persistence.ValidationMode
import javax.persistence.spi.ClassTransformer
import javax.persistence.spi.PersistenceUnitInfo
import javax.persistence.spi.PersistenceUnitTransactionType

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