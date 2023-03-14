package org.needle4k.db.transaction

import org.junit.Assert
import org.junit.Test
import org.needle4k.configuration.DefaultNeedleConfiguration
import org.needle4k.db.Address
import org.needle4k.db.JPAInjectorConfiguration
import org.needle4k.db.Person
import org.needle4k.db.User

class TransactionHelperTest {
  private val configuration = JPAInjectorConfiguration(DefaultNeedleConfiguration.INSTANCE)
  private val objectUnderTest = configuration.transactionHelper
  
  @Test
  fun testLoadAllObjects_WithEntityName() {
    val entity: Person = objectUnderTest.persist(Person().apply { myName = "jens" })
    Assert.assertNotNull(entity.id)
    val loadAllObjects: List<Person> = objectUnderTest.loadAllObjects(Person::class.java)
    Assert.assertEquals(1, loadAllObjects.size)
  }

  @Test
  fun testLoadAllObjects_WithDefaultEntityName() {
    val entity: User = objectUnderTest.persist(User())
    Assert.assertNotNull(entity.id)
    val loadAllObjects: List<User> = objectUnderTest.loadAllObjects(User::class.java)
    Assert.assertEquals(1, loadAllObjects.size)
  }

  @Test
  fun testLoadAllObjects_EmptyResultList() {
    val loadAllObjects: List<Address> = objectUnderTest.loadAllObjects(Address::class.java)
    
    Assert.assertEquals(0, loadAllObjects.size)
  }

  @Test(expected = IllegalArgumentException::class)
  fun testLoadAllObjects_WithUnknownEntity() {
    objectUnderTest.persist(Any())
  }
}