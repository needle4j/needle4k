package org.needle4k.junit5

import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.needle4k.NeedleInjector
import org.needle4k.configuration.DefaultNeedleConfiguration
import org.needle4k.injection.InjectionConfiguration
import org.needle4k.injection.InjectionProvider
import org.needle4k.injection.LazyInjectionProvider

@Suppress("MemberVisibilityCanBePrivate", "unused")
open class NeedleExtension @JvmOverloads constructor(
  val needleInjector: NeedleInjector = NeedleInjector(InjectionConfiguration(DefaultNeedleConfiguration.INSTANCE)),
  vararg injectionProviders: InjectionProvider<*>
) :
  AfterEachCallback, BeforeEachCallback {
  val needleConfiguration get() = needleInjector.configuration.needleConfiguration

  init {
    needleInjector.addInjectionProvider(*injectionProviders)
    needleInjector.addInjectionProvider(LazyInjectionProvider(NeedleInjector::class.java) { needleInjector })
  }

  override fun beforeEach(context: ExtensionContext) {
    needleInjector.initTestInstance(context.requiredTestInstance)
    needleInjector.before()
  }

  override fun afterEach(context: ExtensionContext) {
    needleInjector.after()
  }
}