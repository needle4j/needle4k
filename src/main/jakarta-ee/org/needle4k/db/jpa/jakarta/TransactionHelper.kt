package org.needle4k.db.jpa.jakarta

import jakarta.persistence.EntityManager
import org.needle4k.db.jpa.AbstractTransactionHelper

class TransactionHelper(entityManager: EntityManager) : AbstractTransactionHelper(JpaEntityManager(entityManager)) {
}