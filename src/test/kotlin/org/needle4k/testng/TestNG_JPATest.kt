package org.needle4k.testng

import org.needle4k.db.Person
import org.testng.Assert
import org.testng.annotations.Test

class TestNG_JPATest : DatabaseTestcase() {
  @Test
  fun testGetDBAccess() {
    Assert.assertNotNull(entityManager)
    Assert.assertNotNull(entityManagerFactory)

    val person = Person().apply { myName = "My Name" }
    val tx = entityManager.transaction

    tx.begin()
    entityManager.persist(person)
    val fromDB = entityManager.find(Person::class.java, person.id)
    Assert.assertSame(person, fromDB)
    tx.commit()  }
}