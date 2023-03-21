package org.needle4k.testng

import org.assertj.core.api.Assertions
import org.mockito.Mockito
import org.needle4k.MyComponentBean
import org.needle4k.MyEjbComponent
import org.needle4k.MyEjbComponentBean
import org.needle4k.annotation.InjectIntoMany
import org.needle4k.annotation.ObjectUnderTest
import org.testng.Assert
import org.testng.annotations.Test
import javax.ejb.SessionContext
import javax.inject.Inject
import javax.persistence.EntityTransaction

@Suppress("CdiInjectionPointsInspection")
class NeedleTestNGTest : AbstractNeedleTestcase() {
  @ObjectUnderTest
  private lateinit var componentBean: MyComponentBean

  @InjectIntoMany
  @ObjectUnderTest(implementation = MyEjbComponentBean::class)
  private lateinit var ejbComponent: MyEjbComponent

  private val componentBean1: MyComponentBean = MyComponentBean()

  @ObjectUnderTest
  private val componentBean2: MyComponentBean = componentBean1

  @Inject
  private lateinit var entityTransaction: EntityTransaction

  @Test
  fun testBasicInjection() {
    Assert.assertNotNull(componentBean)
    Assert.assertNotNull(componentBean.entityManager)
    Assert.assertNotNull(componentBean.myEjbComponent)
    Assertions.assertThat(Mockito.mockingDetails(componentBean.sessionContext).isMock).isTrue

    val mock = needleInjector.getInjectedObject(MyEjbComponent::class.java) as MyEjbComponent?
    Assert.assertNotNull(mock)
  }

  @Test
  fun testResourceMock() {
    val sessionContextMock = needleInjector.getInjectedObject(SessionContext::class.java) as SessionContext?
    Assert.assertNotNull(sessionContextMock)
    Assert.assertNotNull(needleInjector.getInjectedObject("queue1"))
    Assert.assertNotNull(needleInjector.getInjectedObject("queue2"))
  }

  @Test
  fun testInjectInto() {
    Assert.assertNotNull(ejbComponent)
    Assert.assertEquals(ejbComponent, componentBean.myEjbComponent)
  }

  @Test
  fun testInitInstance() {
    Assert.assertEquals(componentBean1, componentBean2)
  }

  @Test
  fun testGetEntityManager() {
    Assert.assertNotNull(this.entityManager)
  }

  @Test
  fun testEntityManagerInjection() {
    Assert.assertNotNull(entityManager)
    val entityManager = entityManager
    Assert.assertSame(this.entityManager, entityManager)
  }

  @Test
  fun testEntityTransactionInjection() {
    Assert.assertNotNull(entityTransaction)
  }
}