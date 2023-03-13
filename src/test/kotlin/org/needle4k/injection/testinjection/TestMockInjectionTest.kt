package org.needle4k.injection.testinjection

import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.needle4k.MyEjbComponent
import org.needle4k.annotation.ObjectUnderTest
import org.needle4k.junit4.NeedleRule
import java.net.Authenticator
import javax.ejb.EJB
import javax.inject.Inject

class TestMockInjectionTest {
  @Rule
  @JvmField
  val needleRule = NeedleRule()

  @ObjectUnderTest
  private lateinit var objectUnderTest: TestInjectionComponent

  @Inject
  private lateinit var authenticator: Authenticator

  @EJB
  private lateinit var ejbComponent: MyEjbComponent

  @Test
  fun testInjection() {
    Assert.assertNotNull(authenticator)
    Assert.assertEquals(objectUnderTest.authenticator, authenticator)
    Assert.assertNotNull(ejbComponent)
    Assert.assertEquals(objectUnderTest.ejbComponent, ejbComponent)
  }
}