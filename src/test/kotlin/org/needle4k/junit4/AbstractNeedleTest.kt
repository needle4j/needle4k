package org.needle4k.junit4

import org.assertj.core.api.Assertions.assertThat
import org.junit.Assert
import org.junit.Assert.assertNotNull
import org.mockito.Mockito.mockingDetails
import org.needle4k.MyComponentBean
import org.needle4k.MyEjbComponent
import org.needle4k.MyEjbComponentBean
import org.needle4k.annotation.InjectIntoMany
import org.needle4k.annotation.ObjectUnderTest

abstract class AbstractNeedleTest {
  @ObjectUnderTest
  private lateinit var componentBean: MyComponentBean

  @InjectIntoMany
  @ObjectUnderTest(implementation = MyEjbComponentBean::class)
  private lateinit var ejbComponent: MyEjbComponent

  private val componentBean1 = MyComponentBean()

  @ObjectUnderTest
  private val componentBean2 = componentBean1

  open fun testBasicInjection() {
    assertNotNull(componentBean)
    assertNotNull(componentBean.entityManager)
    assertNotNull(componentBean.myEjbComponent)

    assertThat(mockingDetails(componentBean.entityManager).isMock).isTrue
    assertThat(mockingDetails(componentBean.sessionContext).isMock).isTrue
    assertThat(mockingDetails(componentBean.myEjbComponent).isMock).isFalse

    assertNotNull(ejbComponent)
    Assert.assertEquals(ejbComponent, componentBean.myEjbComponent)
    Assert.assertEquals(componentBean1, componentBean2)

    val entityManagerFactory = componentBean2.entityManagerFactory
    assertNotNull(entityManagerFactory)
  }
}