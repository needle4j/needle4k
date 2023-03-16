package org.needle4k.junit5

import org.needle4k.NeedleInjector
import org.needle4k.configuration.DefaultNeedleConfiguration
import org.needle4k.injection.InjectionConfiguration
import org.needle4k.injection.InjectionProvider
import org.needle4k.injection.LazyInjectionProvider

@Suppress("MemberVisibilityCanBePrivate", "unused")
open class JPANeedleExtension
@JvmOverloads constructor(
  needleInjector: NeedleInjector = NeedleInjector(InjectionConfiguration(DefaultNeedleConfiguration())),
  vararg injectionProviders: InjectionProvider<*>
) : NeedleExtension(needleInjector, *injectionProviders) {

  constructor() : this(NeedleInjector(InjectionConfiguration(DefaultNeedleConfiguration())))

  init {
    addJPAInjectionProvider()
    needleInjector.addInjectionProvider(LazyInjectionProvider(JPANeedleExtension::class.java) { this })
  }
}