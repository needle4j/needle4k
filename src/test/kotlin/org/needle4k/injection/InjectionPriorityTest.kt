package org.needle4k.injection

import org.assertj.core.api.Assertions.assertThat
import org.junit.Assert.assertSame
import org.junit.Rule
import org.junit.Test
import org.needle4k.NeedleSession
import org.needle4k.annotation.ObjectUnderTest
import org.needle4k.junit4.NeedleRule

class InjectionPriorityTest {
  private val injectionProvider = SingletonMapInjectionProvider()

  @Rule
  @JvmField
  val needleRule: NeedleRule = NeedleRule(injectionProvider)

  @ObjectUnderTest
  private lateinit var component: CustomInjectionTestComponent

  @Test
  fun testInjectionProvider() {
    assertSame(injectionProvider.map, component.map)
    assertThat(
      injectionProvider.needleSession.needleInjector.configuration.getInjectionProvider(
        SingletonMapInjectionProvider::class.java
      )
    ).isNotNull
  }
}

@Suppress("UNCHECKED_CAST")
class SingletonMapInjectionProvider @JvmOverloads constructor(val map: Map<Any, Any> = HashMap()) :
  InjectionProvider<Map<*, *>> {
  lateinit var needleSession: NeedleSession

  override fun verify(injectionTargetInformation: InjectionTargetInformation<*>): Boolean {
    return injectionTargetInformation.injectedObjectType === MutableMap::class.java
  }

  override fun getInjectedObject(injectionTargetType: Class<*>) = map

  override fun getKey(injectionTargetInformation: InjectionTargetInformation<*>) =
    injectionTargetInformation.injectedObjectType

  override fun initialize(needleSession: NeedleSession) {
    this.needleSession = needleSession
  }
}