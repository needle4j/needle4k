package org.needle4k.injection

import org.junit.Assert.assertSame
import org.junit.Rule
import org.junit.Test
import org.needle4k.MyConcreteComponent
import org.needle4k.junit4.NeedleRule
import javax.inject.Inject

/**
 * moved from original package to avoid
 */
class InjectionProvidersDefaultInstanceInjectionProviderTest {
  private val instance: MyConcreteComponent = MyConcreteComponent()

  @Rule
  @JvmField
  val needle: NeedleRule = NeedleRule(InjectionProviders.providerForInstance(instance))

  @Inject
  private lateinit var injectedInstance: MyConcreteComponent

  @Test
  fun shouldInjectInstanceA() {
    assertSame(injectedInstance, instance)
  }
}