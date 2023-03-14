package org.needle4k.db

import org.assertj.core.api.Assertions
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.needle4k.junit4.NeedleRule
import javax.inject.Inject
import javax.persistence.EntityManager

class JPAEntityManagerTest {
  @Rule
  @JvmField
  val needleRule = NeedleRule().withJPAInjection()

  @Inject
  private lateinit var entityManager: EntityManager

  @Test
  fun `test with real entity manager`() {
    Assertions.assertThat(Mockito.mockingDetails(entityManager).isMock).isFalse

    val person = Person().apply { myName = "My Name" }
    val entityManager: EntityManager = needleRule.entityManager
    val tx = entityManager.transaction

    Assertions.assertThat(entityManager).isSameAs(this.entityManager)

    tx.begin()
    entityManager.persist(person)
    val fromDB = entityManager.find(Person::class.java, person.id)
    Assert.assertSame(person, fromDB)
    tx.commit()
  }

  @Test
  fun testTransactions() {
    val myEntity = Person().apply { myName = "My Name" }
    val entityManager: EntityManager = needleRule.entityManager
    val transactionHelper = TransactionHelper(entityManager)
    transactionHelper.saveObject(myEntity)
    val fromDb = transactionHelper.loadObject(Person::class.java, myEntity.id)
    Assert.assertFalse(myEntity === fromDb)
  }
}