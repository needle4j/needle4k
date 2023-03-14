package org.needle4k.junit5

import org.junit.Assert
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.needle4k.MyEjbComponent
import org.needle4k.junit4.AbstractNeedleTest
import javax.ejb.SessionContext
import javax.inject.Inject
import javax.persistence.EntityManagerFactory

@ExtendWith(value = [NeedleExtension::class])
class NeedleJUnit5Test : AbstractNeedleTest() {
  @Inject
  private lateinit var needle: NeedleExtension

  @Test
  override fun testBasicInjection() {
    super.testBasicInjection()
  }

  @Test
  fun testMock() {
    Assert.assertNotNull(needle.getInjectedObject(MyEjbComponent::class.java))
    Assert.assertNotNull(needle.getInjectedObject(SessionContext::class.java))
    Assert.assertNotNull(needle.getInjectedObject("queue1"))
    Assert.assertNotNull(needle.getInjectedObject("queue2"))
    Assert.assertNotNull(needle.getInjectedObject(EntityManagerFactory::class.java))
  }
}