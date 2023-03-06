package org.needle4k.db

import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.needle4k.junit4.DatabaseRule
import org.needle4k.junit4.NeedleRule
import javax.inject.Inject
import javax.persistence.EntityManager
import javax.persistence.EntityTransaction

class DatabaseRuleEntityTransactionInjectionTest {
  @Rule
  @JvmField
  var needleRule: NeedleRule = NeedleRule().withOuter(DatabaseRule())

  @Inject
  private lateinit var entityManager: EntityManager

  @Inject
  private lateinit var entityTransaction: EntityTransaction

  @Test
  fun testEntityTransaction() {
    entityTransaction.begin()
    val user1 = User()
    entityManager.persist(user1)
    entityTransaction.commit()
    entityManager.clear()
    entityTransaction.begin()
    val user2 = User()
    entityManager.persist(user2)
    entityTransaction.commit()
    entityManager.clear()

    val user1FromDb = entityManager.find(User::class.java, user1.id)
    Assert.assertEquals(user1.id, user1FromDb.id)
    Assert.assertNotEquals(user1, user1FromDb)
    val user2FromDb = entityManager.find(User::class.java, user2.id)
    Assert.assertEquals(user2.id, user2FromDb.id)
    Assert.assertNotEquals(user2, user2FromDb)
  }
}