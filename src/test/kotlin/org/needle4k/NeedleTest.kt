package org.needle4k

import org.junit.Assert
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.needle4k.annotation.InjectIntoMany
import org.needle4k.annotation.ObjectUnderTest
import org.needle4k.junit4.DatabaseRule
import org.needle4k.junit4.NeedleRule
import javax.ejb.SessionContext
import javax.persistence.EntityManagerFactory

class NeedleTest {
  @Rule
  @JvmField
  var databaseRule = DatabaseRule()

  @Rule
  @JvmField
  var needle = NeedleRule(databaseRule)

  @ObjectUnderTest
  private lateinit var componentBean: MyComponentBean

  @InjectIntoMany
  @ObjectUnderTest(implementation = MyEjbComponentBean::class)
  private lateinit var ejbComponent: MyEjbComponent

  private val componentBean1 = MyComponentBean()

  @ObjectUnderTest
  private val componentBean2 = componentBean1

  @Test
  fun testBasicInjection() {
    assertNotNull(componentBean)
    assertNotNull(componentBean.entityManager)
    assertNotNull(componentBean.myEjbComponent)

    val mock = needle.getInjectedObject(MyEjbComponent::class.java)
    assertNotNull(mock)
  }

  @Test
  fun testResourceMock() {
    val sessionContextMock = needle.getInjectedObject(SessionContext::class.java) as SessionContext
    assertNotNull(sessionContextMock)
    assertNotNull(needle.getInjectedObject("queue1"))
    assertNotNull(needle.getInjectedObject("queue2"))
  }

  @Test
  fun testInjectInto() {
    assertNotNull(ejbComponent)
    Assert.assertEquals(ejbComponent, componentBean.myEjbComponent)
  }

  @Test
  fun testInitInstance() {
    Assert.assertEquals(componentBean1, componentBean2)
  }

  @Test
  fun testEntityManagerFactoryInjection() {
    val entityManagerFactory = componentBean2.entityManagerFactory
    assertNotNull(entityManagerFactory)
    assertNotNull(needle.getInjectedObject(EntityManagerFactory::class.java))
  }
}