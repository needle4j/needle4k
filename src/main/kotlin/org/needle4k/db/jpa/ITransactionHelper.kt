package org.needle4k.db.jpa

internal interface ITransactionHelper {
  /**
   * Saves the given object in the database.
   *
   * @param <T> type of given object obj
   * @param obj object to save
   * @return updated object
   * @throws Exception save objects failed
  </T> */
  @Throws(Exception::class)
  fun <T : Any> saveObject(obj: T): T

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
  fun <T : Any> loadObject(clazz: Class<T>, id: Any): T

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
  fun <T : Any> loadAllObjects(clazz: Class<T>): List<T>

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
  fun <T> executeInTransaction(runnable: Runnable<T>, clearAfterCommit: Boolean): T

  /**
   * see executeInTransaction(runnable, clearAfterCommit) .
   *
   * @param <T>      result type of runnable.run()
   * @param runnable algorithm to execute
   * @return return value of runnable.run()
   * @throws Exception execution failed
  </T> */
  @Throws(Exception::class)
  fun <T> executeInTransaction(runnable: Runnable<T>): T
  fun <T : Any> persist(obj: T): T
}