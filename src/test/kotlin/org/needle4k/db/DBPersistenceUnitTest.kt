package org.needle4k.db

import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.needle4k.junit4.DatabaseRule
import javax.persistence.EntityManager

class DBPersistenceUnitTest {
  @Rule
  @JvmField
  var db: DatabaseRule = DatabaseRule()

  @Test
  fun testDB_withPersistenceUnit() {
    val person = Person().apply { myName = "My Name" }
    val entityManager: EntityManager = db.entityManager

    Assert.assertNotNull(db)
    Assert.assertNotNull(entityManager)
    val tx = entityManager.transaction
    tx.begin()
    entityManager.persist(person)
    val fromDB = entityManager.find(Person::class.java, person.id)
    Assert.assertSame(person, fromDB)
    tx.commit()
  }

  @Test
  @Throws(Exception::class)
  fun testTransactions() {
    val myEntity = Person().apply { myName = "My Name" }
    val entityManager: EntityManager = db.entityManager
    val transactionHelper = TransactionHelper(entityManager)
    transactionHelper.saveObject(myEntity)
    val fromDb = transactionHelper.loadObject(Person::class.java, myEntity.id)
    Assert.assertFalse(myEntity === fromDb)
  }
}