@file:Suppress("UNCHECKED_CAST")

package org.needle4k.db.jpa

import jakarta.persistence.EntityManager
import java.util.*
import jakarta.persistence.Entity

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
  fun <T> saveObject(obj: T): T =
    executeInTransaction { entityManager -> persist(obj, entityManager) }

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
  fun <T> loadObject(clazz: Class<T>, id: Any): T =
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
  fun <T> loadAllObjects(clazz: Class<T>): List<T> {
    val entityAnnotation = clazz.getAnnotation(Entity::class.java)
      ?: throw IllegalArgumentException("Unknown entity: " + clazz.name)

    return executeInTransaction { entityManager ->
      val fromEntity = entityAnnotation.name.ifEmpty { clazz.simpleName }

      entityManager.createQuery("SELECT DISTINCT e FROM $fromEntity e")
        .resultList as List<T>
    }
  }

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
      entityManager.transaction.begin()
      result = runnable(entityManager)
      entityManager.flush()
      entityManager.transaction.commit()

      if (clearAfterCommit) {
        entityManager.clear()
      }
    } finally {
      if (entityManager.transaction.isActive) {
        entityManager.transaction.rollback()
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

  fun <T> persist(obj: T, entityManager: EntityManager): T {
    entityManager.persist(obj)
    return obj
  }

  fun <T> persist(obj: T): T {
    return persist(obj, entityManager)
  }

  fun <T> loadObject(entityManager: EntityManager, clazz: Class<T>?, id: Any?): T {
    return entityManager.find(clazz, id)
  }
}

typealias Runnable<T> = (entityManager: EntityManager) -> T
