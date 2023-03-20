@file:Suppress("UNCHECKED_CAST")
@file:JvmName("TransactionUtil")

package org.needle4k.db

import java.util.*
import javax.persistence.EntityManager

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
  fun <T : Any> saveObject(obj: T): T = execute { it.persist(obj); obj }

  /**
   * Finds and returns the object of the given id in the persistence context.
   *
   * @param <T>   type of searched object
   * @param clazz type of searched object
   * @param id    technical id of searched object
   * @return found object
   * @throws Exception finding object failed
  </T> */
  fun <T : Any> loadObject(clazz: Class<T>, id: Any): T = execute { it.find(clazz, id) as T }

  /**
   * Returns all objects of the given class in persistence context.
   *
   * @param <T>   type of searched objects
   * @param clazz type of searched objects
   * @return list of found objects
   * @throws IllegalArgumentException if the instance is not an entity
   * @throws Exception                loading objects failed
  </T> */
  @Suppress("UNCHECKED_CAST")
  fun <T : Any> loadAllObjects(clazz: Class<T>) =
    execute { it.createQuery("SELECT DISTINCT e FROM ${clazz.name} e").resultList as List<T> }

  /**
   * Encapsulates execution of runnable.run() in transactions.
   *
   * @param <T>              result type of runnable.run()
   * @param action         algorithm to execute
   * @param clearAfterCommit <pre>true</pre> triggers entityManager.clear() after transaction
   * commit
   * @return return value of runnable.run()
   * @throws Exception execution failed
  </T> */
  fun <T> execute(action: Action<T>, clearAfterCommit: Boolean): T {
    val result: T

    try {
      entityManager.beginTransaction()
      result = action(entityManager)
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
   * @param action algorithm to execute
   * @return return value of runnable.run()
   * @throws Exception execution failed
  </T> */
  fun <T> execute(action: Action<T>): T = execute(action, true)
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

typealias Action<T> = (entityManager: EntityManager) -> T
