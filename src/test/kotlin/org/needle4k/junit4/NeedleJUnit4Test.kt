package org.needle4k.junit4

import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.needle4k.MyEjbComponent
import javax.ejb.SessionContext
import javax.persistence.EntityManagerFactory

class NeedleJUnit4Test : AbstractNeedleTest() {
  @Rule
  @JvmField
  var needle = NeedleRule()

  @Test
  override fun testBasicInjection() {
    super.testBasicInjection()
  }

  @Test
  fun testMock() {
    assertNotNull(needle.getInjectedObject(MyEjbComponent::class.java))
    assertNotNull(needle.getInjectedObject(SessionContext::class.java))
    assertNotNull(needle.getInjectedObject("queue1"))
    assertNotNull(needle.getInjectedObject("queue2"))
    assertNotNull(needle.getInjectedObject(EntityManagerFactory::class.java))
  }
}