package org.needle4k.db

import org.needle4k.configuration.NeedleConfiguration
import org.needle4k.db.jpa.TransactionHelper
import org.needle4k.db.operation.AbstractDBOperation
import org.needle4k.db.operation.DBOperation
import org.needle4k.db.operation.NoOperation
import org.slf4j.LoggerFactory
import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory
import javax.persistence.Persistence

class DatabaseInjectorConfiguration(private val needleConfiguration: NeedleConfiguration) {
  val entityManagerFactory: EntityManagerFactory =
    Persistence.createEntityManagerFactory(needleConfiguration.persistenceUnitName)
  val dbOperation = createDBOperation(lookupDBOperationClass(needleConfiguration.dbOperationClassName))
  val entityManager: EntityManager = entityManagerFactory.createEntityManager()
  val transactionHelper = TransactionHelper(entityManager)

  private fun createDBOperation(dbOperationClass: Class<out AbstractDBOperation>): DBOperation {
    try {
      return needleConfiguration.reflectionHelper.createInstance(dbOperationClass, NeedleConfiguration::class.java to needleConfiguration)
    } catch (e: Exception) {
      throw IllegalArgumentException("Could not create a new instance of configured DB operation $dbOperationClass", e)
    }
  }

  private fun lookupDBOperationClass(dbOperation: String): Class<out AbstractDBOperation> = try {
    needleConfiguration.reflectionHelper.lookupClass(AbstractDBOperation::class.java, dbOperation)
      ?: NoOperation::class.java
  } catch (e: Exception) {
    LOG.warn("Error while loading db operation class {}, {}", dbOperation, e.message)
    NoOperation::class.java
  }

  companion object {
    private val LOG = LoggerFactory.getLogger(DatabaseInjectorConfiguration::class.java)
  }
}