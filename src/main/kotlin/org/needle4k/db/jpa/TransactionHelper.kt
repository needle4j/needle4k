@file:Suppress("UNCHECKED_CAST")

package org.needle4k.db.jpa

import jakarta.persistence.EntityManager
import java.util.*

/**
 * Utility class to manage transactions conveniently.
 *
 * @author Heinz Wilming, Alphonse Bendt, Markus Dahm Akquinet AG
 * @author Jan Galinski, Holisticon AG (jan.galinski@holisticon.de)
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
class TransactionHelper(val entityManager: EntityManager) {
  /**
   * Saves the given object in the database.
   *
   * @param <T> type of given object obj
   * @param obj object to save
   * @return updated object
   * @throws Exception save objects failed
  </T> */
  @Throws(Exception::class)
  fun <T : Any> saveObject(obj: T): T = executeInTransaction { entityManager -> persist(obj, entityManager) }

  /**
   * Finds and returns the object of the given id in the persistence context.
   *
   * @param <T>   type of searched object
   * @param clazz type of searched object
   * @param id    technical id of searched object
   * @return found object
   * @throws Exception finding object failed
  </T> */
  @Throws(Exception::class)
  fun <T : Any> loadObject(clazz: Class<T>, id: Any): T =
    executeInTransaction { entityManager -> loadObject(entityManager, clazz, id) }

  /**
   * Returns all objects of the given class in persistence context.
   *
   * @param <T>   type of searched objects
   * @param clazz type of searched objects
   * @return list of found objects
   * @throws IllegalArgumentException if the instance is not an entity
   * @throws Exception                loading objects failed
  </T> */
  @Throws(Exception::class)
  @Suppress("UNCHECKED_CAST")
  fun <T : Any> loadAllObjects(clazz: Class<T>) =
    executeInTransaction { it.createQuery("SELECT DISTINCT e FROM ${clazz.name} e").resultList as List<T> }

  /**
   * Encapsulates execution of runnable.run() in transactions.
   *
   * @param <T>              result type of runnable.run()
   * @param runnable         algorithm to execute
   * @param clearAfterCommit <pre>true</pre> triggers entityManager.clear() after transaction
   * commit
   * @return return value of runnable.run()
   * @throws Exception execution failed
  </T> */
  @Throws(Exception::class)
  fun <T> executeInTransaction(runnable: Runnable<T>, clearAfterCommit: Boolean): T {
    val result: T

    try {
      entityManager.beginTransaction()
      result = runnable(entityManager)
      entityManager.flush()
      entityManager.commitTransaction()

      if (clearAfterCommit) {
        entityManager.clear()
      }
    } finally {
      if (entityManager.isTransactionActive()) {
        entityManager.rollbackTransaction()
      }
    }

    return result
  }

  /**
   * see executeInTransaction(runnable, clearAfterCommit) .
   *
   * @param <T>      result type of runnable.run()
   * @param runnable algorithm to execute
   * @return return value of runnable.run()
   * @throws Exception execution failed
  </T> */
  @Throws(Exception::class)
  fun <T> executeInTransaction(runnable: Runnable<T>): T {
    return executeInTransaction(runnable, true)
  }

  fun <T : Any> persist(obj: T): T {
    return persist(obj, entityManager)
  }

  fun <T : Any> persist(obj: T, entityManager: EntityManager): T {
    entityManager.persist(obj)
    return obj
  }

  fun <T : Any> loadObject(entityManager: EntityManager, clazz: Class<T>, id: Any): T {
    return entityManager.find(clazz, id)
  }
}

fun EntityManager.beginTransaction() {
  transaction.begin()
}

fun EntityManager.commitTransaction() {
  transaction.commit()
}

fun EntityManager.rollbackTransaction() {
  transaction.rollback()
}

fun EntityManager.isTransactionActive() = transaction.isActive

typealias Runnable<T> = (entityManager: EntityManager) -> T
