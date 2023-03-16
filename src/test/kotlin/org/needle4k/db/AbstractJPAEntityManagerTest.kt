package org.needle4k.db

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.Assert
import org.junit.Assert.assertSame
import org.mockito.Mockito
import javax.inject.Inject
import javax.persistence.EntityManager

abstract class AbstractJPAEntityManagerTest {
  @Inject
  protected lateinit var entityManager: EntityManager

  open fun `test with real entity manager`() {
    assertThat(Mockito.mockingDetails(entityManager).isMock).isFalse

    val person = Person().apply { myName = "My Name" }
    val tx = entityManager.transaction

    tx.begin()
    entityManager.persist(person)
    val fromDB = entityManager.find(Person::class.java, person.id)
    assertSame(person, fromDB)
    tx.commit()
  }

  open fun testTransactions() {
    val myEntity = Person().apply { myName = "My Name" }
    val transactionHelper = TransactionHelper(entityManager)
    transactionHelper.saveObject(myEntity)

    val fromDb = transactionHelper.loadObject(Person::class.java, myEntity.id)
    Assert.assertFalse(myEntity === fromDb)
  }
}