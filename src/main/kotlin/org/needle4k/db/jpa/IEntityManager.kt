package org.needle4k.db.jpa

interface IEntityManager {
  fun beginTransaction()
  fun commitTransaction()
  fun isTransactionActive(): Boolean
  fun rollbackTransaction()

  fun flush()
  fun clear()
  fun persist(obj: Any): Any
  fun <T> find(entityClass: Class<T>, primaryKey: Any): T
  fun <T> loadAllObjects(clazz: Class<T>): List<T>
}