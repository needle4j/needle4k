package org.needle4k.junit4

import org.assertj.core.api.Assertions.assertThat
import org.junit.Assert
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mockingDetails
import org.needle4k.MyComponentBean
import org.needle4k.MyEjbComponent
import org.needle4k.MyEjbComponentBean
import org.needle4k.annotation.InjectIntoMany
import org.needle4k.annotation.ObjectUnderTest
import javax.ejb.SessionContext
import javax.persistence.EntityManagerFactory

class NeedleTest {
  @Rule
  @JvmField
  var needle = NeedleRule().withJPAInjection()

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

    assertThat(mockingDetails(componentBean.entityManager).isMock).isFalse
    assertThat(mockingDetails(componentBean.sessionContext).isMock).isTrue
    assertThat(mockingDetails(componentBean.myEjbComponent).isMock).isFalse

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