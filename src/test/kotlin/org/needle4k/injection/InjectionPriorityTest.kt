package org.needle4k.injection

import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.needle4k.annotation.ObjectUnderTest
import org.needle4k.junit4.NeedleRule

class InjectionPriorityTest {
  private val map: Map<Any, Any> = HashMap()

  private val injectionProvider: InjectionProvider<Map<Any, Any>> = object : CustomMapInjectionProvider() {
    @Suppress("UNCHECKED_CAST")
    override fun <T> getInjectedObject(injectionTargetType: Class<T>): T = map as T
  }

  @Rule
  @JvmField
  val needleRule: NeedleRule = NeedleRule(injectionProvider)

  @ObjectUnderTest
  private lateinit var component: CustomInjectionTestComponent

  @Test
  fun testInjectionProviderPriority() {
    Assert.assertSame(map, component.map)
  }
}