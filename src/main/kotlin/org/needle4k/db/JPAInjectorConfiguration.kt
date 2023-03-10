package org.needle4k.db

import org.hibernate.Session
import org.hibernate.jdbc.ReturningWork
import org.hibernate.jdbc.Work
import org.needle4k.configuration.NeedleConfiguration
import org.needle4k.db.operation.AbstractDBOperation
import org.needle4k.db.operation.DBOperation
import org.needle4k.db.operation.NoOperation
import org.slf4j.LoggerFactory
import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory
import javax.persistence.Persistence

class JPAInjectorConfiguration(val needleConfiguration: NeedleConfiguration) {
  val entityManagerFactory: EntityManagerFactory =
    Persistence.createEntityManagerFactory(needleConfiguration.persistenceUnitName)
  val dbOperation = createDBOperation(lookupDBOperationClass(needleConfiguration.dbOperationClassName))
  val entityManager: EntityManager = entityManagerFactory.createEntityManager()
  val transactionHelper = TransactionHelper(entityManager)

  private val hibernateSession: Session get() = entityManager.delegate as Session

  fun execute(runnable: Work) {
    hibernateSession.doWork {
      it.autoCommit = false

      runnable.execute(it)

      if (!it.isClosed) {
        it.commit()
      }
    }
  }

  fun <T> execute(runnable: ReturningWork<T>): T? {
    return hibernateSession.doReturningWork {
      it.autoCommit = false
      val result: T? = runnable.execute(it)

      if (!it.isClosed) {
        it.commit()
      }

      result
    }
  }

  private fun createDBOperation(dbOperationClass: Class<out AbstractDBOperation>): DBOperation {
    try {
      return needleConfiguration.reflectionUtil.createInstance(
        dbOperationClass,
        JPAInjectorConfiguration::class.java to this
      )
    } catch (e: Exception) {
      throw IllegalArgumentException("Could not create a new instance of configured DB operation $dbOperationClass", e)
    }
  }

  private fun lookupDBOperationClass(dbOperation: String): Class<out AbstractDBOperation> = try {
    needleConfiguration.reflectionUtil.lookupClass(AbstractDBOperation::class.java, dbOperation)
      ?: NoOperation::class.java
  } catch (e: Exception) {
    LOG.warn("Error while loading db operation class {}, {}", dbOperation, e.message)
    NoOperation::class.java
  }

  companion object {
    private val LOG = LoggerFactory.getLogger(JPAInjectorConfiguration::class.java)
  }
}