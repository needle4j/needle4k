package org.needle4k.junit5

import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.needle4k.AbstractNeedleRule
import org.needle4k.NeedleInjector
import org.needle4k.configuration.DefaultNeedleConfiguration
import org.needle4k.injection.InjectionConfiguration
import org.needle4k.injection.InjectionProvider

@Suppress("MemberVisibilityCanBePrivate", "unused")
open class NeedleExtension @JvmOverloads constructor(
  needleInjector: NeedleInjector = NeedleInjector(InjectionConfiguration(DefaultNeedleConfiguration.INSTANCE)),
  vararg injectionProviders: InjectionProvider<*>
) : AbstractNeedleRule(needleInjector, *injectionProviders), AfterEachCallback, BeforeEachCallback {
  override fun beforeEach(context: ExtensionContext) {
    runBeforeTest(context.requiredTestInstance)
  }

  override fun afterEach(context: ExtensionContext) {
    runAfterTest()
  }
}