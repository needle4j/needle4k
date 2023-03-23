package org.needle4k.injection

import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.needle4k.annotation.ObjectUnderTest
import org.needle4k.junit4.NeedleRule

class InjectionPriorityTest {
  private val injectionProvider= SingletonMapInjectionProvider()

  @Rule
  @JvmField
  val needleRule: NeedleRule = NeedleRule(injectionProvider)

  @ObjectUnderTest
  private lateinit var component: CustomInjectionTestComponent

  @Test
  fun testInjectionProviderPriority() {
    Assert.assertSame(injectionProvider.map, component.map)
  }
}