package org.needle4k.db.jpa.jakarta

import jakarta.persistence.EntityManager
import org.needle4k.db.jpa.IEntityManager

@Suppress("UNCHECKED_CAST")
class JpaEntityManager(entityManager: EntityManager) : IEntityManager, EntityManager by entityManager {
  override fun beginTransaction() {
    transaction.begin()
  }

  override fun commitTransaction() {
    transaction.commit()
  }

  override fun isTransactionActive() = transaction.isActive

  override fun rollbackTransaction() {
    transaction.rollback()
  }

  override fun <T> loadAllObjects(clazz: Class<T>) =
    createQuery("SELECT DISTINCT e FROM ${clazz.name} e").resultList as List<T>
}