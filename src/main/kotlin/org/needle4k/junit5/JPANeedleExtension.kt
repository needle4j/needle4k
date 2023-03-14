package org.needle4k.junit5

import org.needle4k.NeedleInjector
import org.needle4k.configuration.DefaultNeedleConfiguration
import org.needle4k.injection.InjectionConfiguration
import org.needle4k.injection.InjectionProvider

@Suppress("MemberVisibilityCanBePrivate", "unused")
open class JPANeedleExtension(needleInjector: NeedleInjector, vararg injectionProviders: InjectionProvider<*>) :
  NeedleExtension(needleInjector, *injectionProviders) {

  constructor() : this(NeedleInjector(InjectionConfiguration(DefaultNeedleConfiguration())))

  init {
    withJPAInjection()
  }
}